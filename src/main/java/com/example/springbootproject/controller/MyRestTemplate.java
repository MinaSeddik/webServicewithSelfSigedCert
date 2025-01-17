package com.example.springbootproject.controller;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Timed
public class MyRestTemplate {

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/rest")
    public String callRest() {

        System.out.println("*** callRest API Started! Thread Name: " + Thread.currentThread().getName());

        String uri = "http://localhost:8080/async3";

        String result = "xxx";
//        String result = restTemplate.getForObject(uri, String.class);
        System.out.println(result);

        System.out.println("*** callRest API Completed! Thread Name: " + Thread.currentThread().getName());

        return result + result + result;
    }
}
