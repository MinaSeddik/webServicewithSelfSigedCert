package com.example.springbootproject.controller;

import com.example.springbootproject.service.MyRepoService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.QueryParam;
import java.time.LocalTime;
import java.util.UUID;

@RestController
@Slf4j
@Timed
public class MyRepoController {
    @Autowired
    public MyRepoService myRepoService;

    @Timed(value = "my-app.myrepo.timed")
    @RequestMapping(value = "/repo")
    public String getRepo() {

        log.info("this is info log");
        log.trace("this is trace log");
        log.debug("this is debug log");


        String str = myRepoService.getAllItems();
        return str;
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
    public String searchRepo(@QueryParam("q") String value) {

        log.info("Received Search with query-param = {}", value);
        String str = myRepoService.getAllItems();
        return str;
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
