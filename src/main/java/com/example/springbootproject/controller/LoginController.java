package com.example.springbootproject.controller;

import com.example.springbootproject.dto.RestResponse;
import com.example.springbootproject.domain.Credential;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.Instant;

@RestController
@Slf4j
public class LoginController {

    @Autowired
    public AuthenticationManager authenticationManager;

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@Valid @ModelAttribute("credential") Credential credential,
                                   BindingResult result) {

        log.info("Welcome to Myapp login ... ");
        if (result.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(result);
        }

        log.info("Welcome to Myapp login ... credential = {}", credential);
        log.info("username: {}, password: {}", credential.getUsername(), credential.getPassword());


        Authentication authentication =
                new UsernamePasswordAuthenticationToken(credential.getUsername(), credential.getPassword());

        authentication = authenticationManager.authenticate(authentication);
        log.info("authentication ... " + authentication);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Successful login for username: {}", credential.getUsername());
        RestResponse restResponse = RestResponse.builder()
                .timestamp(Instant.now().toString())
                .status(HttpStatus.OK.toString())
                .username(authentication.getName())
                .authorities(authentication.getAuthorities().toString())
                .build();


        return ResponseEntity.ok()
                .body(restResponse);

    }


    // NOT used; see WebSecurityConfig Configuration
    @Deprecated()
    @RequestMapping(value = "/logout")
    public ResponseEntity<RestResponse> logout(HttpServletRequest request,
                                               HttpServletResponse response,
                                               Authentication authentication) {

        log.info("log out username: {}", authentication.getName());

        try {
            request.logout();
        } catch (ServletException e) {
        }

        for (Cookie cookie : request.getCookies()) {
            String cookieName = cookie.getName();
            Cookie cookieToDelete = new Cookie(cookieName, null);
            cookieToDelete.setMaxAge(0);
            response.addCookie(cookieToDelete);
        }

        log.info("Successful logout for username: {}", authentication.getName());
        RestResponse restResponse = RestResponse.builder()
                .timestamp(Instant.now().toString())
                .status(HttpStatus.OK.toString())
                .username(authentication.getName())
                .message("USer logged out successfully.")
                .build();


        return ResponseEntity.ok()
                .body(restResponse);

    }


}