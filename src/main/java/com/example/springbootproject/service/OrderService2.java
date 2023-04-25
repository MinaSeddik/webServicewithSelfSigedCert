package com.example.springbootproject.service;

import com.example.springbootproject.domain.Order;
import com.example.springbootproject.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderService2 {


    public String createOrder(Order order) {

        // create order - the normal flow
        // ....

        // Recoverable exception raised and need retrial

        // (1) mark order status to be UNDER-RETRIAL
        // (2) persist it into the db and set attempt = 0

        int attempt = 0;
        order.setAttempts(attempt);
        log.info("Create Order : Attempt: {}", attempt);

//        retryCreateOrder(order);

        return "Not created yet, under-retrial";
    }


    /*
     * How it works ?!
     *
     * delay = 60000L -> first attempt initial delay - wait for 1 minute
     * second attempt  = multiplier * initial delay - 2 * 60000L = wait for 2 minutes
     * 3rd attempt  = multiplier * previous delay - 2 * 2 minutes  = wait for 4 minutes
     * 4th attempt  = multiplier * previous delay - 2 * 4 minutes  = wait for 8 minutes
     * 5th attempt  = multiplier * previous delay - 2 * 8 minutes  = wait for 16 minutes
     * 6th attempt  = multiplier * previous delay - 2 * 16 minutes  = wait for 32 minutes
     * ....
     *
     * maxDelay - > max wait time
     *
     *
     * PLEASE READ ME (Reference: https://dzone.com/articles/how-to-create-asynchronous-and-retryable-methods-with-failover-support)
     * ----------------
     * Thread pool task-based implementation: This implementation is based on ThreadPoolTaskExecutor
     * without keeping the task executor thread busy during the whole retry processing, unlike
     * the combination of @Async (see "Spring Boot - Async methods") and @Retryable
     * (see "Retry Handling With Spring-Retry"). Indeed, when using the Spring traditional retry
     * annotation, the thread that runs the method performs the whole retry policy, including
     * waiting periods, and remains busy until the end. For example, if the ThreadPoolTaskExecutor
     * has 10 threads with a retry policy that may take 5 minutes, and the application receives 30
     * requests, only 10 requests can be processed simultaneously and the others will be blocked for
     * the whole 5 minutes. So the execution of the 30 requests may take 15 minutes.
     *
     *
     *
     * */
    @Retryable(include = {MyException.class}, maxAttempts = 5,
            backoff = @Backoff(delay = 60000L, maxDelay = 6000000L, multiplier = 2))
    public void retryCreateOrder(Order order) {

        // (1) order ID
        // (2) read its data from db
        // (3) read attempt number
        int attempt = order.getAttempts();
        attempt++;
        order.setAttempts(attempt);
        log.info("Retry Create Order : Attempt: {}", attempt);
        log.info("RetryCreating Order {} - Retry Number: {}",
                order.getOrderCode(), RetrySynchronizationManager.getContext().getRetryCount());

        // create order - the normal flow
        // if pass: mark order as NEW status and log the attempt then return

        // if failed, retry if allowed < MAX_ATTEMPTS reached
        // log the attempt in the db
        throw new MyException("test retrial");

    }

    @Recover
    public void recover(MyException e, Order sql) throws SQLException {

        log.info("Calling recover ...");
//        throw e;
    }


    public void scheduleAllRetrials() {
//        List<Order> underRetrialOrders = retrieveOrderUnderRetrial();

        // just simulation
        List<Order> underRetrialOrders = new ArrayList<>();

        underRetrialOrders.stream().forEach(o -> {
            int attempt = 3; // read it from the db

            retryCreateOrder(o);
        });
    }

}
