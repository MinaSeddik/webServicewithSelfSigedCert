package com.example.springbootproject.repository.impl;

import com.example.springbootproject.domain.BankAccount;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactiveAccountRepository extends ReactiveCrudRepository<BankAccount, Integer> {
}