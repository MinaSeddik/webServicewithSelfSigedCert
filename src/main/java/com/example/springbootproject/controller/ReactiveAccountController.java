package com.example.springbootproject.controller;

import com.example.springbootproject.model.AccountRequest;
import com.example.springbootproject.service.ReactiveAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ReactiveAccountController {

    @Autowired
    private ReactiveAccountService reactiveAccountService;

    @RequestMapping(value = "/update-account", method = RequestMethod.GET,
//            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> updateAccount() {

        return reactiveAccountService.updateAccount(new AccountRequest())
                .doOnNext(b -> log.info("Account controller: {}", b));

    }

    @RequestMapping(value = "/update-account-transaction", method = RequestMethod.GET,
//            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> updateAccountWithTransaction() {

        return reactiveAccountService.updateAccountWithTransaction(new AccountRequest())
                .doOnNext(b -> log.info("Account controller: {}", b));

    }

}
