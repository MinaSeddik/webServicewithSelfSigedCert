package com.example.springbootproject.controller;

import com.example.springbootproject.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class OrderController {



    /*

        curl -X POST -H "Content-Type: application/json"  \
                --data '{"orderId":2,"orderType":"fbi", "applicantName":"Mina Monmon"}' \
                http://localhost:8080/orders

    */

    @PostMapping("/orders")
    public String createOrder(@RequestBody Order order) {

        log.info("order class type: {}", order.getClass());

        log.info("Order {}", order);

        return "Order " + order.getClass() + " created successfully!";
    }


}
