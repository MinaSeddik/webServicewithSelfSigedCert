package com.example.springbootproject.controller;

import com.example.springbootproject.domain.FbiOrder;
import com.example.springbootproject.domain.Order;
import com.example.springbootproject.service.OrderRetryService;
import com.example.springbootproject.service.OrderRetryService2;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class OrderRetryController {

    @Autowired
    private OrderRetryService2 orderRetryService;

    @GetMapping("/create-order-retry")
    public String createOrder() throws SchedulerException {

        log.info("Create an Order");
        // simulate received Order
        Order order = new FbiOrder();
        order.setOrderId(485);

        log.info("order class type: {}", order.getClass());
        log.info("Order {}", order);

        orderRetryService.createOrder(order);

        return "Order " + order.getClass() + " is Under Retrial!";
    }


}
