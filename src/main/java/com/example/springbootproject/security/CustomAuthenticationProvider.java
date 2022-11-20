package com.example.springbootproject.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Profile("custom-auth")
@Component
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        log.info("Authenticating User: {}", authentication.getName());

        String password = authentication.getCredentials().toString();
        log.debug("Authenticate User: {} with password: {}",
                authentication.getName(), Strings.isBlank(password) ? Strings.EMPTY : "****");



        UserDetails userDetails = null;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            log.warn("username: {} doesn't exist, throwing UsernameNotFoundException.", username);
            log.info("Authenticate User: {} ... [Failed]", authentication.getName());
            throw e;
        }


//        userDetails.isAccountNonLocked();

        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            log.info("Authenticate User: {} ... [Pass]", authentication.getName());
            return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
        } else {
            log.warn("The given password of username: {} doesn't match, throwing BadCredentialsException.", username);
            log.info("Authenticate User: {} ... [Failed]", authentication.getName());
            throw new BadCredentialsException("Username or Password isn't correct!");
        }
    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        return authenticationType.equals(UsernamePasswordAuthenticationToken.class);
    }

}