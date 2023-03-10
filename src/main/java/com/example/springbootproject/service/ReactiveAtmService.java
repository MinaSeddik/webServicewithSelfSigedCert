package com.example.springbootproject.service;

import com.example.springbootproject.domain.BankAccount;
import com.example.springbootproject.repository.impl.ReactiveAtmRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Service
@Slf4j
public class ReactiveAtmService {

    @Autowired
    private ReactiveAtmRepository reactiveAtmRepository;


    public Flux<BankAccount> findAllBankAccounts() {

        return reactiveAtmRepository.findAllBankAccounts()
                .doOnNext(a -> log.info("ReactiveAtmService::findAllBankAccounts {}", a));
    }

    public Mono<BankAccount> findBankAccountByName(String firstName) {

        return reactiveAtmRepository.findBankAccountByName(firstName)
                .doOnNext(a -> log.info("ReactiveAtmService::findBankAccountByName {}", a));
    }

    public Flux<String> addBankAccountWithNewBranch() {

        return reactiveAtmRepository.addBankAccountWithNewBranch()
                .doOnNext(s -> log.info("ReactiveAtmService::addBankAccount {}", s));
    }

    public Flux<String> addBankBranchWithFailure() {

        return reactiveAtmRepository.addBankBranchWithFailure()
                .doOnNext(s -> log.info("ReactiveAtmService::addBankAccount {}", s))
                .doOnError(th -> log.error("Exception: ", th));
    }


    public Flux<String> addBankAccountWithTransaction() {

        Flux<String> flux = Flux.fromIterable(Arrays.asList("my name is mina".split(" ")))
                .map(s -> "_" + s + "_")
                .doOnNext(s -> log.info("*** {} ***", s));


        return reactiveAtmRepository.addBankAccountWithTransaction(flux)
                .zipWith(flux).flatMap(data -> Flux.just(data.getT1(), data.getT2()))
                .doOnNext(s -> log.info("ReactiveAtmService::addBankAccount {}", s))
                .doOnError(th -> log.error("Exception: ", th));
    }


}
