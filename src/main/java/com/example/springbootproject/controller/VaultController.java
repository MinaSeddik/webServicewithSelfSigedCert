package com.example.springbootproject.controller;

import com.example.springbootproject.domain.AppUser;
import com.example.springbootproject.dto.UserInfo;
import com.example.springbootproject.service.OptService;
import com.example.springbootproject.service.VaultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
public class VaultController {

    @Autowired
    private VaultService vaultService;

    @GetMapping(value = "/vault")
    public String vault() {

        log.info("Inside VaultController ...");

        String out = vaultService.tryVault();

        return out;
    }


}
