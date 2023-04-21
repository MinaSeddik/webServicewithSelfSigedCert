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

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private static final int MAX_FAILED_ATTEMPTS = 10;
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

        // check if the Account is Locked
        if(! user.isAccountNonLocked()){
            // handle Locked Account

            // 1. try to unlock the Account; assuming that it was locked for 24 hrs
            // ,so we need to check the 24 hrs have passed or not
            long currentTime = new Date().getTime();
            long lockedAt = user.getLockedTime().getTime();

            if(lockedAt + (24*60*60*1000) >= currentTime){
                // so the 24 hours has passed

                // unlock the Account
                user.setAccountNonLocked(true);
                user.setFailedAttempts(0);
                user.setLockedTime(null);
                myAppUserDetailsService.updateUser(user);
            }else{
                // handle Locked Account
                // send error message to the user notifying him that the Account is locked
                // dont mentioned the any information about the locking policy or even
                // the max attempt nor the lock time

                // This Account is Locked, please try again later
            }
        }

        // check if the Account is NOT Active
        if (!user.isEnabled()){
            // handle InActive Account
        }

        if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return new UsernamePasswordAuthenticationToken(user.getUsername(),
                    user.getPassword(), user.getAuthorities());
        } else {
            log.warn("Invalid Password for user: {}", user.getUsername());
//            throw new BadCredentialsException("Username or Password isn't correct!");

            user.setFailedAttempts(user.getFailedAttempts() + 1);
            if( user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                // lock the Account
                user.setAccountNonLocked(false);
                user.setLockedTime(new Date());
            }
            myAppUserDetailsService.updateUser(user);


            throw new InValidCredentialsException(HttpStatus.UNAUTHORIZED, "Username or Password isn't correct!");
        }

    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthentication.class.isAssignableFrom(aClass);
    }

}





