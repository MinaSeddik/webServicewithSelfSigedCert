package com.example.springbootproject.controller;

import com.example.springbootproject.service.GeneralPurposeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class GeneralPurposeController {

    @Autowired
    private GeneralPurposeService generalPurposeService;

    @GetMapping("/a1")
    public String callStoredProc() throws ExecutionException, InterruptedException {
        log.info("callStoredProc ... ");

        generalPurposeService.callStoredProc();

        return "OK";
    }
}
