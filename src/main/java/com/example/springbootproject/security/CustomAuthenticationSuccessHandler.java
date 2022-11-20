package com.example.springbootproject.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(
                                        HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Authentication authentication)
                                        throws IOException {

        log.info("Inside CustomAuthenticationSuccessHandler");
        var authorities = authentication.getAuthorities();
        var auth =
                authorities.stream()
                        .filter(a -> isAuthorized(a))
                        .findFirst();
        if (auth.isPresent()) {
            log.info("Redirects to home page");
            httpServletResponse.sendRedirect("/home.html");
        } else {
            log.info("Redirects to error page");
            httpServletResponse.sendRedirect("/error");
        }
    }

    private boolean isAuthorized(GrantedAuthority a) {
        return a.getAuthority().equals("READ") || a.getAuthority().equals("WRITE");
    }
}