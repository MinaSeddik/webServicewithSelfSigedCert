package com.example.springbootproject.repository;

import com.example.springbootproject.model.BankAccount;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactiveAccountRepository extends ReactiveCrudRepository<BankAccount, Integer> {
}