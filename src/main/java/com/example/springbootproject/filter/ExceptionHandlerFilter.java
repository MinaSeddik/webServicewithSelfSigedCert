package com.example.springbootproject.filter;

import com.example.springbootproject.dto.RestErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

@Component
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Autowired
    public ObjectMapper mapper;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        RestErrorResponse errorResponse = null;
        try {
            filterChain.doFilter(request, response);
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

}