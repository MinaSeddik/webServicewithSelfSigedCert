package com.example.springbootproject.controller;

import com.example.springbootproject.domain.CaOrder;
import com.example.springbootproject.domain.FbiOrder;
import com.example.springbootproject.domain.P2cOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class OrderController2 {


    /*

        curl -X POST -H "Content-Type: application/json"  \
                --data '{"orderId":2,"orderType":"fbi", "applicantName":"Mina Monmon"}' \
                http://localhost:8080/create-fbi-order

    */
    @PostMapping("/create-fbi-order")
    public String createFbiOrder(@RequestBody FbiOrder order) {

        log.info("Order {}", order);

        return "Order " + order.getClass() + " created successfully!";
    }


    /*

        curl -X POST -H "Content-Type: application/json"  \
                --data '{"orderId":2,"orderType":"ca", "applicantName":"Mina Monmon"}' \
                http://localhost:8080/create-ca-order

    */
    @PostMapping("/create-ca-order")
    public String createCaOrder(@RequestBody CaOrder order) {

        log.info("Order {}", order);

        return "Order " + order.getClass() + " created successfully!";
    }


    /*

        curl -X POST -H "Content-Type: application/json"  \
                --data '{"orderId":2,"orderType":"p2c", "applicantName":"Mina Monmon"}' \
                http://localhost:8080/create-p2c-order

    */
    @PostMapping("/create-p2c-order")
    public String createP2cOrder(@RequestBody P2cOrder order) {

        log.info("Order {}", order);

        return "Order " + order.getClass() + " created successfully!";
    }
}
