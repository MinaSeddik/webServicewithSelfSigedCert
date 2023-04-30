package com.example.springbootproject.filter;

import com.example.springbootproject.dto.RestErrorResponse;
import com.example.springbootproject.exception.InValidCredentialsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Profile("rl")
@Component
@Slf4j
public class ExceptionHandlerAndRateLimiterFilter extends OncePerRequestFilter {

    private static final int MAX_ATTEMPT = 10;
    @Autowired
    private ObjectMapper mapper;

    // here we can implement Rate limit
    private LoadingCache<String, Integer> attemptsCache;

    @PostConstruct
    private void initializeAttemptsCache() {
        attemptsCache = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.DAYS)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(final String key) {
                        return 0;
                    }
                });
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        RestErrorResponse errorResponse;
        try {

            // Note: this technique doesn't work in a distributed/clustered env

            // if is Blocked IP
            if (isBlocked(request)) {
                // return error message, the IP Address is block due to frequent, unusual requests,
                // please try again later

//                Rejecting the request (HTTP 429 Too Many Requests)
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            }


            filterChain.doFilter(request, response);
        } catch (ResponseStatusException ex) {


            // check that Exception type you want the Attempts Handling policy to handle
            if (ex.getCause().getClass().isInstance(InValidCredentialsException.class)) {
                reportFailureLoginAttempt(request);
            }


            log.error("{} ", ex.getMessage(), ex);
            response.setStatus(ex.getStatus().value());
            errorResponse = RestErrorResponse.builder()
                    .timestamp(Instant.now().toString())
                    .status(ex.getStatus().toString())
                    .error(ex.getReason())
                    .path(request.getServletPath())
                    .build();

            response.getWriter()
                    .write(mapper.writeValueAsString(errorResponse));

        } catch (RuntimeException ex) {
            log.error("{}: {} ", ex.getClass().getName(), ex.getMessage(), ex);
            throw ex;
        }

    }

    private boolean isBlocked(HttpServletRequest request) {
        try {
            return attemptsCache.get(getClientIP(request)) >= MAX_ATTEMPT;
        } catch (Exception ex) {
            return false;
        }
    }

    private String getClientIP(HttpServletRequest request) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    public void reportFailureLoginAttempt(HttpServletRequest request) {
        loginFailed(getClientIP(request));
    }

    public void loginFailed(String key) {
        int attempts;
        try {
            attempts = attemptsCache.get(key);
        } catch (Exception e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }
}