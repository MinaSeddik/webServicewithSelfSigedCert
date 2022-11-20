package com.example.springbootproject.service;

import com.example.springbootproject.model.BankAccount;
import com.example.springbootproject.repository.impl.BankAccountWithCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BankAccountWithCacheService {

    @Autowired
    private BankAccountWithCacheRepository bankAccountWithCacheRepository;

    public List<BankAccount> getAllAccounts() {

        log.info("getAllAccounts Service called");
        return bankAccountWithCacheRepository.getAllAccounts();
    }

    public BankAccount getAccountById(int id) {

        log.info("getAccountById Service called");
        return bankAccountWithCacheRepository.getAccountById(id);
    }

    public BankAccount getAccountByName(String firstName) {

        log.info("getAccountByName Service called");
        return bankAccountWithCacheRepository.getAccountByName(firstName);
    }
}
