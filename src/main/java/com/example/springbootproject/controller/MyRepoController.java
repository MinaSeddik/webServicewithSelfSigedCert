package com.example.springbootproject.controller;

import com.example.springbootproject.service.MyRepoService;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyRepoController {
    @Autowired
    public MyRepoService myRepoService;

    @Timed(value = "my-app.myrepo.timed")
    @RequestMapping(value = "/repo")
    public String getRepo() {
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
