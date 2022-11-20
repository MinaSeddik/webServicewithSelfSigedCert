package com.example.springbootproject.service;

import com.example.springbootproject.exception.InValidCredentialsException;
import com.example.springbootproject.model.SecurityUserDetails;
import com.example.springbootproject.security.OtpAuthentication;
import com.example.springbootproject.security.UsernamePasswordAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class OtpAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private MyAppUserDetailsService myAppUserDetailsService;

    @Autowired
    private OptService optService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String code = String.valueOf(authentication.getCredentials());

        log.info("Authenticate user: {} with OTP token: {}", username, code);

        SecurityUserDetails user = myAppUserDetailsService.loadUserByUsername(username);
        log.debug("SecurityUserDetails for user: {} is {}", username, Objects.isNull(user) ? "NULL-Value" : "Valid");

        if(!user.is2faEnabled()){
            throw new BadCredentialsException("2FA is NOT supported for user: {}" + username);
        }

        boolean verified = optService.verifyCode(user.getOptSecretKey(), code);

        if (verified) {
            log.warn("OTP code for user: {} is VALID", user.getUsername());
            return new UsernamePasswordAuthenticationToken(user.getUsername(),
                    user.getPassword(), user.getAuthorities());
        } else {
            log.warn("Invalid OTP code for user: {}", user.getUsername());
//            throw new BadCredentialsException("Username or Password isn't correct!");
            throw new InValidCredentialsException(HttpStatus.UNAUTHORIZED, "OTP code isn't correct!");
        }

    }

    @Override
    public boolean supports(Class<?> aClass) {
        return OtpAuthentication.class.isAssignableFrom(aClass);
    }

}
