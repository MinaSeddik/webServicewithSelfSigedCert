package com.example.springbootproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//@Controller
@RestController
@Slf4j
public class MainController {

    /*
    @GetMapping("/")
    public Map<String, String> main(HttpServletRequest request, Authentication authentication) {

        Map<String, String> data = new HashMap<String, String>();

        log.info("--------> Here in MainController ... {}", request);
        log.info("--------> Here in MainController ... {}", authentication);

        if (Objects.nonNull(authentication)) {
            log.info("inside frm not null object");
            data.put("username", authentication.getName());
            data.put("authorities", authentication.getAuthorities().toString());
        }

        return data;
    }
*/

    @GetMapping("/")
    public void main(HttpServletResponse response) throws IOException {
        response.sendRedirect("/repo");
    }

}