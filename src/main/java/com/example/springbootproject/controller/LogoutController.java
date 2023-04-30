package com.example.springbootproject.controller;

import com.example.springbootproject.dto.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;


@RestController
@Slf4j
public class LogoutController {

    @RequestMapping(value = "/logout")
    public ResponseEntity<RestResponse> logout(HttpServletRequest request,
                                               HttpServletResponse response,
                                               Authentication authentication) {

        log.info("log out username: {}", authentication.getName());

        try {
            request.logout();
        } catch (ServletException e) {
            log.error("Exception: ", e);
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
