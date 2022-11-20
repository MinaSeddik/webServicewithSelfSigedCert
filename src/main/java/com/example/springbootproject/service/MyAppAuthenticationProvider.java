package com.example.springbootproject.service;

import com.example.springbootproject.exception.InValidCredentialsException;
import com.example.springbootproject.model.SecurityUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class MyAppAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private MyAppUserDetailsService myAppUserDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SCryptPasswordEncoder sCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        log.info("Authenticate user: {}", username);

        SecurityUserDetails user = myAppUserDetailsService.loadUserByUsername(username);
        log.debug("SecurityUserDetails for user: {} is {}", username, Objects.isNull(user) ? "NULL-Value" : "Valid");


        switch (user.getUser().getAlgorithm()) {
            case BCRYPT:
                return checkPassword(user, password, bCryptPasswordEncoder);
            case SCRYPT:
                return checkPassword(user, password, sCryptPasswordEncoder);
        }

        log.warn("Invalid Password for user: {}", username);
//        throw new BadCredentialsException("Username or Password isn't correct!");
        throw new InValidCredentialsException(HttpStatus.UNAUTHORIZED, "Username or Password isn't correct!");
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private Authentication checkPassword(SecurityUserDetails user, String rawPassword, PasswordEncoder encoder) {

        log.info("Check password Encoding: {}", encoder.getClass().toString());

        log.info("Raw pwd: {}", rawPassword);
        log.info("stored pwd: {}", user.getPassword());

        if (encoder.matches(rawPassword, user.getPassword())) {
            return new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getPassword(),
                    user.getAuthorities());
        } else {
            log.warn("Invalid Password for user: {}", user.getUsername());
//            throw new BadCredentialsException("Username or Password isn't correct!");
            throw new InValidCredentialsException(HttpStatus.UNAUTHORIZED, "Username or Password isn't correct!");
        }
    }


}