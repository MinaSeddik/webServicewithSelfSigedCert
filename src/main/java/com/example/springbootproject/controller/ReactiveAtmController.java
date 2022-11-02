package com.example.springbootproject.controller;


import com.example.springbootproject.model.BankAccount;
import com.example.springbootproject.service.ReactiveAtmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ReactiveAtmController {

    @Autowired
    private ReactiveAtmService reactiveAtmService;


    @RequestMapping(value = "/atm", method = RequestMethod.GET,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//            produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<BankAccount> atm1() {

        return reactiveAtmService.findAllBankAccounts()
                .doOnNext(b -> log.info("Atm controller: {}", b));

    }

    @RequestMapping(value = "/atm2", method = RequestMethod.GET,
//            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<BankAccount> atm2(@RequestParam(name = "fn", defaultValue = "mina") String firstName) {

        return reactiveAtmService.findBankAccountByName(firstName)
                .doOnNext(b -> log.info("Atm controller: {}", b));

    }

    @RequestMapping(value = "/atm-add", method = RequestMethod.GET,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//            produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<String> addBankAccountWithNewBranch() {

        return reactiveAtmService.addBankAccountWithNewBranch()
                .doOnNext(b -> log.info("Atm controller: {}", b));

    }

    @RequestMapping(value = "/atm-add-fail", method = RequestMethod.GET,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//            produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<String> addBankBranchWithFailure() {

        return reactiveAtmService.addBankBranchWithFailure()
                .doOnNext(b -> log.info("Atm controller: {}", b));

    }


    @RequestMapping(value = "/atm-add-trans", method = RequestMethod.GET,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//            produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<String> addBankAccountWithTransaction() {

        return reactiveAtmService.addBankAccountWithTransaction()
                .doOnNext(b -> log.info("Atm controller: {}", b));

    }

}
