package com.example.springbootproject.service;

import com.example.springbootproject.domain.MyOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderCreationRetrialService orderCreationRetrialService;


    public String createOrder(MyOrder order) {

        // create order - the normal flow
        // ....

        // Recoverable exception raised and need retrial

        // (1) mark order status to be UNDER-RETRIAL
        // (2) persist it into the db and set attempt = 0

        int attempt = 0;
        order.setAttempts(attempt);
        log.info("Create Order : Attempt: {}", attempt);

        Runnable task = () -> retryCreateOrder(order);
        orderCreationRetrialService.scheduleRetrial(task, attempt);


        return "Not created yet, under-retrial";
    }

    private final ConcurrentMap<String, Boolean> map = new ConcurrentHashMap<String, Boolean>();

    private void forceRetryCreateOrder(MyOrder order) {

        if (map.putIfAbsent(order.getOrderCode(), true) == null) {
            // it means that the key doesnt exist

            try {
                // do force retrial logic
            } catch (Exception ex) {
                // handle exception accordingly
            } finally {
                map.remove(order.getOrderCode());
            }

            //
        } else {
            // the order is under processing of retrial

//            raise Exception
//            throw OrderUnderRetrialException();
        }

    }

    private void retryCreateOrder(MyOrder order) {

        // (1) order ID
        // (2) read its data from db
        // (3) read attempt number
        int attempt = order.getAttempts();
        attempt++;
        order.setAttempts(attempt);
        log.info("Retry Create Order : Attempt: {}", attempt);

        // create order - the normal flow
        // if pass: mark order as NEW status and log the attempt then return

        // if failed, retry if allowed < MAX_ATTEMPTS reached
        // log the attempt in the db

        Runnable task = () -> retryCreateOrder(order);
        orderCreationRetrialService.scheduleRetrial(task, attempt);

    }

    public void scheduleAllRetrials() {
//        List<Order> underRetrialOrders = retrieveOrderUnderRetrial();

        // just simulation
        List<MyOrder> underRetrialOrders = new ArrayList<>();

        underRetrialOrders.stream().forEach(o -> {
            int attempt = 3; // read it from the db

            Runnable task = () -> retryCreateOrder(o);
            orderCreationRetrialService.scheduleRetrial(task, attempt);
        });
    }

}
