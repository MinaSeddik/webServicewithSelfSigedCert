package com.example.springbootproject.security;

import com.example.springbootproject.audit.EventAction;
import com.example.springbootproject.audit.event.LoginFailureEventAction;
import com.example.springbootproject.dto.RestErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationEventPublisher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Instant;

@Component("customAuthenticationEntryPoint")
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        log.info("Handle global Security exception through AuthenticationEntryPoint");

        // to properly log the error
        log.error("getCause: {}", authException.getCause());
        log.error("Message: {}", authException.getMessage());
        log.error("class: {}", authException.getClass());
        log.error("authException: {}", authException);
        log.error("--> ", authException);


        // report, log and audit this event
        EventAction failedLoginEventAction = LoginFailureEventAction.builder()
                .message(authException.getMessage())
                .build();
        applicationEventPublisher.publishEvent(failedLoginEventAction);


        RestErrorResponse restError = RestErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .status(HttpStatus.UNAUTHORIZED.toString())
                .error(authException.getMessage())  // security issue
                .path(request.getServletPath())
                .build();

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.getAttributeNames().asIterator().forEachRemaining(s ->
                    log.info("{} {}", s, session.getAttribute(s)));

            log.info("Call session.invalidate() ...");
            session.invalidate();
        }
        SecurityContextHolder.clearContext();


        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), restError);

//        responseStream.flush();
    }
}