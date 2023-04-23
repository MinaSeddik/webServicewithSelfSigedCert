package com.example.springbootproject.controller;

import com.example.springbootproject.audit.EventAction;
import com.example.springbootproject.audit.EventLoggingService;
import com.example.springbootproject.audit.event.OrderCreationSuccessEventAction;
import com.example.springbootproject.domain.Order;
import com.example.springbootproject.domain.OrderType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
public class OrderController {

    @Autowired
    private EventLoggingService eventLoggingService;

    @GetMapping(value = "/create-order")
    public Order createOrder() {

        log.info("Inside Create Order...");

        // .... do some logic to create Order - success

        Order order = Order.builder()
                .orderId(1)
                .orderCode(UUID.randomUUID().toString())
                .orderType(OrderType.FBI)
                .applicantName("John Smith")
                .build();

        String paymentCode = "Stripe_" + UUID.randomUUID().toString().replaceAll("-", "");

        // simulate Create order and log the event
        EventAction orderCreationEventAction = OrderCreationSuccessEventAction.builder()
                .order(order)
                .paymentCode(paymentCode)
                .build();

        eventLoggingService.logEvent(orderCreationEventAction);

        return order;
    }
}
