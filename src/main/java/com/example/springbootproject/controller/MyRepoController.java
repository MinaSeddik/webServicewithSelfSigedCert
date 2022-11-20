package com.example.springbootproject.controller;

import com.example.springbootproject.service.MyRepoService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@Slf4j
@Timed
public class MyRepoController {
    @Autowired
    public MyRepoService myRepoService;

    @Timed(value = "my-app.myrepo.timed")
    @RequestMapping(value = "/repo")
    public Map<String, String> getRepo(Authentication authentication) {

        log.info("this is info log");
        log.trace("this is trace log");
        log.debug("this is debug log");


        Map<String, String> data = myRepoService.getAllItems();

//        SecurityContext context = SecurityContextHolder.getContext();
//        Authentication authentication = context.getAuthentication();
        if (Objects.nonNull(authentication)) {
            data.put("username", authentication.getName());
            data.put("authorities", authentication.getAuthorities().toString());
        }

        return data;
    }

    @DeleteMapping(value = "/repo")
    public Map<String, String> deleteRepo(Authentication authentication) {

        log.info("Inside delete repo ...");

        Map<String, String> data = new HashMap<>();
        data.put("repo", "repo-name");
        data.put("status", "deleted");

        if (Objects.nonNull(authentication)) {
            data.put("username", authentication.getName());
            data.put("authorities", authentication.getAuthorities().toString());
        }

        return data;
    }

    @PostMapping(value = "/repo")
    @CrossOrigin("http://localhost:8080")
    public Map<String, String> postRepo(Authentication authentication) {

        log.info("Inside post repo ...");

        Map<String, String> data = new HashMap<>();
        data.put("repo", "repo-name");
        data.put("status", "deleted");

        if (Objects.nonNull(authentication)) {
            data.put("username", authentication.getName());
            data.put("authorities", authentication.getAuthorities().toString());
        }

        return data;
    }

    @RequestMapping(value = "/auth")
    public ResponseEntity<String> auth() {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Token", UUID.randomUUID().toString());

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body("Ok");
    }

    @Timed(value = "my-app.myrepo.search.timed")
    @RequestMapping(value = "/repo/search")
    public Map<String, String> searchRepo(@QueryParam("q") String value) {

        log.info("Received Search with query-param = {}", value);
        Map<String, String> data = myRepoService.getAllItems();
        return data;
    }

    @RequestMapping(value = "/trans")
    public String trans() {
        String str = myRepoService.trans1();
        return str;
    }

    @RequestMapping(value = "/trans2")
    public String trans2() {
        String str = myRepoService.trans2();
        return str;
    }

    @RequestMapping(value = "/trans3")
    public String trans3() {
        String str = myRepoService.trans3();
        return str;
    }


}
