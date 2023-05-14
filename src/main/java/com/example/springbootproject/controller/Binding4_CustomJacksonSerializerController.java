package com.example.springbootproject.controller;

import com.example.springbootproject.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class Binding4_CustomJacksonSerializerController {

    /*

    curl -X POST -H "Content-Type: application/json"  \
            --data '{"orderId":2,"orderType":"fbi"}' \
            http://localhost:8080/bind4-create-order

    */

    @PostMapping("/bind4-create-order")
    public String bind4(@RequestBody Order order) {

        log.info("order class type: {}", order.getClass());
        log.info("order Id: {}", order.getOrderId());
        log.info("order type: {}", order.getOrderType());

        return "Order " + order.getClass() + " created successfully!";
    }
}
