package com.example.springbootproject.util;

import com.example.springbootproject.domain.Invoice;
import com.example.springbootproject.repository.impl.BankAccountWithCacheRepository;

import java.util.function.Predicate;

public class InvoicePredicate implements Predicate<Invoice> {

    private BankAccountWithCacheRepository bankAccountWithCacheRepository;

    public InvoicePredicate(BankAccountWithCacheRepository bankAccountWithCacheRepository) {
        this.bankAccountWithCacheRepository = bankAccountWithCacheRepository;
    }

    @Override
    public boolean test(Invoice invoice) {
        return false;
    }
}
