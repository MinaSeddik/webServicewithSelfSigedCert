package com.example.springbootproject.controller;

import com.example.springbootproject.domain.Person2;
import com.example.springbootproject.service.JpaPersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class JpaPersonController {

    @Autowired
    private JpaPersonService jpaPersonService;

    @GetMapping("/persons")
    public Iterable<Person2> getPersons() {
        log.info("getPersons ... ");

        return jpaPersonService.getPersons();
    }

}
