package com.example.springbootproject.controller;

import com.example.springbootproject.domain.BookSearchCriteria;
import com.example.springbootproject.domain.OrderType;
import com.example.springbootproject.mvc.editor.CustomIntegerEditor;
import com.example.springbootproject.mvc.editor.CustomLocalDateEditor;
import com.example.springbootproject.mvc.editor.CustomOrderTypeEditor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@Slf4j
public class Binding1_InitBindingController {

    @InitBinder
    public void initBinder(WebDataBinder binder) {

        binder.registerCustomEditor(OrderType.class, new CustomOrderTypeEditor());
        binder.registerCustomEditor(LocalDate.class, new CustomLocalDateEditor());
    }

    //    http://localhost:8080/bind1-ordertype?orderType=fbi

    @GetMapping("/bind1-ordertype")
    public String bind1(OrderType orderType) {

        log.info("Binding1_InitBindingController, received value: {}", orderType);

        return "Value received: " + orderType.name();
    }


    //    http://localhost:8080/bind1-localdate?localDate=05-14-2023

    @GetMapping("/bind1-localdate")
    public String bind1(LocalDate localDate) {

        log.info("Binding1_InitBindingController, received value: {}", localDate);

        return "Value received: " + localDate;
    }


}
