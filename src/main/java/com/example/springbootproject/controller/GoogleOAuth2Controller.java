package com.example.springbootproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
public class GoogleOAuth2Controller {

    @GetMapping("/login/oauth2/google")
    public void oauth2Google(HttpServletRequest request) {
        log.info("Google Responded ... ");

        log.info(request.getMethod() + " "+ request.getParameter("code"));

    }

    @PostMapping("/login/oauth2/google")
    public void oauth2Googlepo(HttpServletRequest request) {
        log.info("Google Responded ... ");

        log.info(request.getMethod() + " "+ request.getParameter("code"));

    }
}
