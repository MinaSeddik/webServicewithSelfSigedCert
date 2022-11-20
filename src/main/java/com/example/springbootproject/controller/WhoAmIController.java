package com.example.springbootproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;


@RestController
@Slf4j
public class WhoAmIController {

    @RequestMapping(value = "/whoami")
    public Map<String, String> whoAmI(HttpServletRequest request) {

        log.info("Welcome to Who am I? Controller ...");

        HttpSession session = request.getSession();
        String jSessionId = session.getId();
        SecurityContext securityContext = (SecurityContext) session.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        Authentication authentication = securityContext.getAuthentication();

//        SecurityContext context = SecurityContextHolder.getContext();
//        Authentication authentication = context.getAuthentication();

        Map<String, String> userDataInfo = new HashMap<>();
        userDataInfo.put("JSESSIONID", jSessionId);


        if (Objects.nonNull(authentication)) {
            userDataInfo.put("username", authentication.getName());
            userDataInfo.put("authorities", authentication.getAuthorities().toString());
        }

        return userDataInfo;
    }

}
