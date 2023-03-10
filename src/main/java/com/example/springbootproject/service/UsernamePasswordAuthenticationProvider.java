package com.example.springbootproject.service;

import com.example.springbootproject.exception.InValidCredentialsException;
import com.example.springbootproject.domain.SecurityUserDetails;
import com.example.springbootproject.security.UsernamePasswordAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private MyAppUserDetailsService myAppUserDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        log.info("Authenticate user: {}", username);

        SecurityUserDetails user = myAppUserDetailsService.loadUserByUsername(username);
        log.debug("SecurityUserDetails for user: {} is {}", username, Objects.isNull(user) ? "NULL-Value" : "Valid");

        if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return new UsernamePasswordAuthenticationToken(user.getUsername(),
                    user.getPassword(), user.getAuthorities());
        } else {
            log.warn("Invalid Password for user: {}", user.getUsername());
//            throw new BadCredentialsException("Username or Password isn't correct!");
            throw new InValidCredentialsException(HttpStatus.UNAUTHORIZED, "Username or Password isn't correct!");
        }

    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthentication.class.isAssignableFrom(aClass);
    }

}





