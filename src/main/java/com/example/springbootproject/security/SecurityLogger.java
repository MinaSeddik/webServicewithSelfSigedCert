package com.example.springbootproject.security;

import com.example.springbootproject.audit.EventAction;
import com.example.springbootproject.audit.EventLoggingService;
import com.example.springbootproject.audit.event.AuthorizationFailureEventAction;
import com.example.springbootproject.audit.event.LoginFailureEventAction;
import com.example.springbootproject.audit.event.LoginSuccessEventAction;
import com.example.springbootproject.audit.event.LogoutSuccessEventAction;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class SecurityLogger {

    @Autowired
    private EventLoggingService eventLoggingService;

    @EventListener
    public void authenticated(final @NonNull AuthenticationSuccessEvent event) {
        final Object principal = event.getAuthentication().getPrincipal();
        log.info("Successful login - [username: \"{}\"]", principal);

        EventAction succeededLoginEventAction = LoginSuccessEventAction.builder()
                .message(String.format("Successful login - [username: \"%s\"]", principal))
                .build();
        eventLoggingService.logEvent(succeededLoginEventAction);
    }

    @EventListener
    public void authenticationFailure(final @NonNull AbstractAuthenticationFailureEvent event) {
        final Object principal = event.getAuthentication().getPrincipal();
        log.info("Unsuccessful login - [username: \"{}\"]", principal);

        EventAction failedLoginEventAction = LoginFailureEventAction.builder()
                .message(String.format("Unsuccessful login - [username: \"%s\"]", principal))
                .build();
        eventLoggingService.logEvent(failedLoginEventAction);
    }

    @EventListener
    public void authorizationFailure(final @NonNull AuthorizationFailureEvent event) {
        final Object principal = event.getAuthentication().getPrincipal();
        final String message = event.getAccessDeniedException().getMessage();
        log.error("Unauthorized access - [username: \"{}\", message: \"{}\"]", principal,
                Optional.ofNullable(message).orElse("<null>"));

        EventAction authorizationFailureEventAction = AuthorizationFailureEventAction.builder()
                .message(String.format("Unauthorized access - [username: \"%s\", message: \"%s\"]", principal,
                        Optional.ofNullable(message).orElse("<null>")))
                .build();
        eventLoggingService.logEvent(authorizationFailureEventAction);
    }

    @EventListener
    public void logoutSuccess(final @NonNull LogoutSuccessEvent event) {
        final Object principal = event.getAuthentication().getPrincipal();
        log.info("Successful logout - [username: \"{}\"]", principal);

        EventAction logoutSuccessEventAction = LogoutSuccessEventAction.builder()
                .message(String.format("Successful logout - [username: \"%s\"]", principal))
                .build();
        eventLoggingService.logEvent(logoutSuccessEventAction);
    }
}