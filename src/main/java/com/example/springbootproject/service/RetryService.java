package com.example.springbootproject.service;

import com.example.springbootproject.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RetryService {

//    @Retryable(include = {SQLException.class}, maxAttemptsExpression = "${retry.maxAttempts}",
//            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
//    public void createOrder(String sql) throws SQLException {
//
//    }


    @Retryable(include = {MyException.class}, maxAttempts = 5, backoff = @Backoff(delay = 8000))
    public void createOrder(String orderCode) throws MyException {

        log.info("Creating Order {} - Retry Number: {}", orderCode, RetrySynchronizationManager.getContext().getRetryCount());

        throw new MyException("retrial test");

    }

    @Recover
    public void recover(MyException e, String orderCode) {
        log.info("Recover from Order Creation {} - Retry Number: {}", orderCode, RetrySynchronizationManager.getContext().getRetryCount());
    }


}
