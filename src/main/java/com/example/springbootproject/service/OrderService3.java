package com.example.springbootproject.service;

import com.example.springbootproject.domain.Order;
import com.example.springbootproject.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class OrderService3 {

    @Autowired
    private OrderCreationRetrialService2 orderCreationRetrialService2;

    public String createOrder(Order order) {

        // create order - the normal flow
        // ....

        // Recoverable exception raised and need retrial

        // (1) mark order status to be UNDER-RETRIAL
        // (2) persist it into the db and set attempt = 0

        int attempt = 0;
        order.setAttempts(attempt);
        log.info("Create Order : Attempt: {}", attempt);

        orderCreationRetrialService2.scheduleRetrial(() -> retryCreateOrder(order));

        return "Not created yet, under-retrial";
    }


    private Order retryCreateOrder(Order order) {

        // (1) order ID
        // (2) read its data from db
        // (3) read attempt number
        int attempt = order.getAttempts();
        attempt++;
        order.setAttempts(attempt);
        log.info("Retry Create Order : Attempt: {}", attempt);
//        log.info("RetryCreating Order {} - Retry Number: {}",
//                order.getOrderCode(), RetrySynchronizationManager.getContext().getRetryCount());

        // create order - the normal flow
        // if pass: mark order as NEW status and log the attempt then return

        // if failed, retry if allowed < MAX_ATTEMPTS reached
        // log the attempt in the db
        throw new MyException("test retrial");

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
