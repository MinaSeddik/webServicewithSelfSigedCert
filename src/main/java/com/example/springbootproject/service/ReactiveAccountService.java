package com.example.springbootproject.service;

import com.example.springbootproject.exception.InvalidUserException;
import com.example.springbootproject.model.AccountRequest;
import com.example.springbootproject.model.AuditRecord;
import com.example.springbootproject.model.BankAccount;
import com.example.springbootproject.repository.ReactiveAccountRepository;
import com.example.springbootproject.repository.ReactiveAuditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ReactiveAccountService {

    @Autowired
    private ReactiveAccountRepository reactiveAccountRepository;
    @Autowired
    private ReactiveAuditRepository reactiveAuditRepository;

//    @Autowired
//    private TransactionalOperator transactionalOperator;

    public Mono<Void> updateAccount(AccountRequest request) {
        return reactiveAccountRepository.findById(request.getAccount())
                .doOnNext(bankAccount -> bankAccount.setBalance(bankAccount.getBalance() + request.getAmount()))
                .flatMap(reactiveAccountRepository::save)
//                .thenReturn(toEvent(request))
                .flatMap(this::toEvent)
                .flatMap(reactiveAuditRepository::save)
                .doOnError(th -> log.error("Exception: ", th))
                .then();
    }

    private AuditRecord toEvent(AccountRequest request) {

        return AuditRecord.builder()
                .event("Update Account " + request.getAccount() + " to " + request.getAmount())
                .build();

    }

    private Mono<AuditRecord> toEvent(BankAccount bankAccount) {

        AuditRecord auditRecord = AuditRecord.builder()
                .event("Update Account " +
                        bankAccount.getFirstName() +
                        " to " +
                        bankAccount.getBalance())
                .build();

        return Mono.just(auditRecord);
    }

    @Transactional
    public Mono<Void> updateAccountWithTransaction(AccountRequest request) {

        return reactiveAccountRepository.findById(request.getAccount())
                .doOnNext(bankAccount -> bankAccount.setBalance(bankAccount.getBalance() + request.getAmount()))
                .flatMap(reactiveAccountRepository::save)
//                .flatMap(this::toEvent_transaction)
                .flatMap(this::toEvent)
                .flatMap(reactiveAuditRepository::save)
                .handle((auditRecord, sink) -> {
                    if (!this.isValid(auditRecord)) {
                        sink.error(new InvalidUserException());
                    } else if (isSendable(auditRecord)) {
                        sink.next(auditRecord);
                    } else {
                        //just ignore element
                    }
                })
                .flatMap(v -> Mono.error(new RuntimeException("to test transaction failure!")))
                .then();

    }

    private boolean isSendable(AuditRecord auditRecord) {
        return true;
    }

    private boolean isValid(AuditRecord auditRecord) {
        return true;
    }


    private Mono<AuditRecord> toEvent_transaction(BankAccount bankAccount) {

        AuditRecord auditRecord = AuditRecord.builder()
                .event(null)
                .build();

        return Mono.just(auditRecord);
    }

}

