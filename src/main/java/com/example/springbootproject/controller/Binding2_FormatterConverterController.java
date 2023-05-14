package com.example.springbootproject.controller;

import com.example.springbootproject.domain.BookStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class Binding2_FormatterConverterController {

    //    http://localhost:8080/bind2-bookstatus?bookStatus=new

    // using global converter StringToBookStatusEnumConverter
    @GetMapping("/bind2-bookstatus")
    public String bind2(BookStatus bookStatus) {

        log.info("Binding2_FormatterConverterController, received value: {}", bookStatus);

        // Watch out: may raise NullPointerException if bookStatus is null (bookStatus is invalid value)
        return "Value received: " + bookStatus.name();
    }

}
