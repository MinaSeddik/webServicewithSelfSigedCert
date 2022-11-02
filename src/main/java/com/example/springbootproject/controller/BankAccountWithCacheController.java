package com.example.springbootproject.controller;


import com.example.springbootproject.model.BankAccount;
import com.example.springbootproject.service.BankAccountWithCacheService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.QueryParam;
import java.util.List;

@RestController
@Slf4j
@Timed
public class BankAccountWithCacheController {

    @Autowired
    private BankAccountWithCacheService bankAccountWithCacheService;

    @RequestMapping(value = "/all-accounts", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BankAccount> getAllAccounts() {

        return bankAccountWithCacheService.getAllAccounts();
    }

    @RequestMapping(value = "/account-id", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BankAccount getAccountById(@QueryParam("first-name") int id) {

        return bankAccountWithCacheService.getAccountById(id);
    }

    @RequestMapping(value = "/account-name", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BankAccount getAccountByName(@QueryParam("first-name") String firstName) {

        return bankAccountWithCacheService.getAccountByName(firstName);
    }

}
