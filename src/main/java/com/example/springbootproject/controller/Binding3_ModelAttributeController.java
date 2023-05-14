package com.example.springbootproject.controller;

import com.example.springbootproject.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@Slf4j
public class Binding3_ModelAttributeController {


    @ModelAttribute("order")
    public Order getItem(final HttpServletRequest request) throws IOException {

        String type = request.getParameter("type");

        if (type != null) {
            if (type.equalsIgnoreCase(OrderType.FBI.name())) {
                return new FbiOrder();
            }
            if (type.equalsIgnoreCase(OrderType.P2C.name())) {
                return new P2cOrder();
            }
            if (type.equalsIgnoreCase(OrderType.CA.name())) {
                return new CaOrder();
            }
        }
        return null;
    }

    //    http://localhost:8080/bind3-create-order?type=fbi

    @GetMapping("/bind3-create-order")
    public String createOrder(@ModelAttribute("order") Order order) {

        log.info("order class type: {}", order.getClass());

        return "Order " + order.getClass() + " created successfully!";
    }


}
