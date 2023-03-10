package com.example.springbootproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class MainController {

    @GetMapping("/")
//    public String main(OAuth2AuthenticationToken token) {
    public String main() {

//        log.info(String.valueOf(token.getPrincipal()));
        return "main.html";
    }
}