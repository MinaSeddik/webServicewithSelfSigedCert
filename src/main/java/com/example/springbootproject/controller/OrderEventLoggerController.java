package com.example.springbootproject.controller;

import com.example.springbootproject.audit.EventAction;
import com.example.springbootproject.audit.EventLoggingService;
import com.example.springbootproject.audit.event.OrderCreationSuccessEventAction;
import com.example.springbootproject.domain.MyOrder;
import com.example.springbootproject.domain.OrderType;
import com.example.springbootproject.service.OrderService;
import com.example.springbootproject.service.OrderService2;
import com.example.springbootproject.service.OrderService3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
public class OrderEventLoggerController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderService2 orderService2;

    @Autowired
    private OrderService3 orderService3;

    @Autowired
    private EventLoggingService eventLoggingService;

    @GetMapping(value = "/create-order-retrial")
    public String createOrder_retrial() {

        MyOrder order = MyOrder.builder()
                .orderId(10)
                .orderCode(UUID.randomUUID().toString())
                .orderType(OrderType.FBI)
                .applicantName("John Smith - test retry")
                .build();

//        return orderService.createOrder(order);


//        return orderService2.createOrder(order);


//        String x = orderService2.createOrder(order);
//        orderService2.retryCreateOrder(order);
//        return x;

        String r = orderService3.createOrder(order);
        return r;
    }

    @GetMapping(value = "/create-order")
    public MyOrder createOrder() {

        log.info("Inside Create Order...");

        // .... do some logic to create Order - success

        MyOrder order = MyOrder.builder()
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
