package com.example.springbootproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
public class EnvironmentController {

    @Value("${app.myproject.env}")
    private String envValue;

    @Value("${app.myproject.global}")
    private String glbValue;

    @GetMapping("/env")
    public String getEnv() {
        log.info("getEnv Responded ... " + envValue);
        log.info("getEnv Responded ... " + glbValue);


        return glbValue + "  -  " + envValue;
    }
}
