package com.example.springbootproject.service;

import com.example.springbootproject.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.springbootproject.retrial.OrderCreationScheduler;

import static org.quartz.JobBuilder.newJob;

@Service
@Slf4j
public class OrderRetryService2 {


    @Autowired
    private OrderCreationScheduler orderCreationScheduler;


    public void createOrder(Order order) throws SchedulerException {

        // normal steps to create the order
        // ...

        // assume that the order failed due to specific known Exception

        // handle retrial policy
//        orderCreationScheduler.scheduleOrderCreationRetry(order.getOrderId(), 1);
        orderCreationScheduler.scheduleOrderCreationRetry(order.getOrderId(), 1);

    }

    public void retryOrder(int id, int retry){

    }



}
