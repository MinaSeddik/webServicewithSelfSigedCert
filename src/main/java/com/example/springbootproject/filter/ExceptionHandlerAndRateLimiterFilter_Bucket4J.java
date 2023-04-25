package com.example.springbootproject.filter;

import com.example.springbootproject.dto.RestErrorResponse;
import com.example.springbootproject.exception.InValidCredentialsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


//https://www.baeldung.com/spring-bucket4j
@Component
@Slf4j
public class ExceptionHandlerAndRateLimiterFilter_Bucket4J extends OncePerRequestFilter {

    private static final int MAX_ATTEMPT = 20;
    @Autowired
    private ObjectMapper mapper;

    // here we can implement Rate limit
//    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private LoadingCache<String, Bucket> attemptsCache;

    @PostConstruct
    private void initializeAttemptsCache() {

//        Max = 20 request / minute
        attemptsCache = Caffeine.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Bucket>() {
                    @Override
                    public Bucket load(final String key) {
                        Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));

//                        OR
//                        Bandwidth limit = Bandwidth.classic(20, Refill.intervally(20, Duration.ofMinutes(1)));
                        return Bucket.builder()
                                .addLimit(limit)
                                .build();
                    }
                });
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        RestErrorResponse errorResponse;
        try {

            // Note: this technique doesn't work in a distributed/clustered env

            String requestIp = getClientIP(request);
            Bucket bucket = attemptsCache.get(requestIp);
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            if (probe.isConsumed()) {

                log.info("A token is consumed for IP: {}, probe = {}", requestIp, probe);
                filterChain.doFilter(request, response);

                // it's better to NOT use the below header, for security reason
                // No need to give the hacker an information for how the Rate limit is behaving
                response.setHeader("X-Rate-Limit-Remaining", Long.toString(probe.getRemainingTokens()));
                return;
            }

            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;

            String traceId = UUID.randomUUID().toString();
            log.info("{} - Request IP: {} is NOT allowed to create an Order, All tokens are consumed, should wait {} seconds",
                    traceId, requestIp, waitForRefill);

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));

            errorResponse = RestErrorResponse.builder()
                    .timestamp(Instant.now().toString())
                    .traceId(traceId)
                    .status(HttpStatus.TOO_MANY_REQUESTS.toString())
                    .error("Acceded rate limit threshold")
                    .path(request.getServletPath())
                    .build();

            response.getWriter()
                    .write(mapper.writeValueAsString(errorResponse));

        } catch (ResponseStatusException ex) {

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

    private String getClientIP(HttpServletRequest request) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        return !request.getServletPath().equals("/create-order");
    }
}