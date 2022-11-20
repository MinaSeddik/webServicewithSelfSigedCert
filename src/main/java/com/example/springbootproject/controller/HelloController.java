package com.example.springbootproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletRequest;
import java.util.Objects;


/*
Mind that it is not a @RestController but a simple @Controller. Because of this,
Spring does not send the value returned by the method in the HTTP response.
Instead, it finds and renders the view with the name home.html.
 */
@Controller
@Slf4j
public class HelloController {

    @GetMapping("/home")
    public String home() {
        return "home.html";
    }

    @GetMapping("/csrf-attack")
    public String csrf_attack(ServletRequest request, ModelMap model) {

        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        if(Objects.isNull(csrfToken)){
            throw new RuntimeException("Missing CSRF token!");
        }

        model.addAttribute("_csrf", csrfToken);

//        model.addAttribute("_csrf.parameterName", csrfToken.getParameterName());
//        model.addAttribute("_csrf.token", csrfToken.getToken());

        return "csrf-attack.html";
    }

}