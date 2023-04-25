package com.example.springbootproject.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RobustWithRetractableService {


    @Retryable(value = SQLException.class)
    public void retryServiceWithRecovery(String sql) throws SQLException {
        log.info("Calling retryServiceWithRecovery ...");
        fail();
    }

    // There will be up to two attempts and a delay of 100 milliseconds.
    @Retryable(value = SQLException.class, maxAttempts = 2, backoff = @Backoff(delay = 3000))
    public void retryServiceWithCustomization(String sql) throws SQLException {
        log.info("Calling retryServiceWithCustomization ...");
        fail();
    }

    // externalize the values of delay and maxAttempts into a properties file.
    @Retryable(value = SQLException.class, maxAttemptsExpression = "${app.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${app.retry.maxDelay}"))
    public void retryServiceWithExternalConfiguration(String sql) throws SQLException {
        log.info("Calling retryServiceWithExternalConfiguration ...");
        fail();
    }

    @Retryable(value = {SQLException.class}, maxAttempts = 4,
            backoff = @Backoff(delay = 1000, maxDelay = 5000, multiplier = 2))
    public void retryMethodWithoutRandomDelay(String sql) throws CannotAcquireLockException, SQLException {
        log.info("Calling retryMethodWithoutRandomDelay ...");
        fail();
    }

    @Retryable(value = {SQLException.class}, maxAttempts = 4,
            backoff = @Backoff(random = true, delay = 1000, maxDelay = 5000, multiplier = 2))
    public void retryMethodWitRandomDelay(String sql) throws CannotAcquireLockException, SQLException {
        log.info("Calling retryMethodWitRandomDelay ...");
        fail();
    }

    @Recover
    public void recover(SQLException e, String sql) throws SQLException {

        log.info("Calling recover ...");
        throw e;
    }

    public void fail() throws SQLException {
        throw new SQLException("...");
    }

    public void pass() {

        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException e) {
        }
    }


}
