package com.example.springbootproject.controller;

import com.example.springbootproject.domain.BookSearchCriteria;
import com.example.springbootproject.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ExceptionController {

    //    http://localhost:8080/exception

    @GetMapping("/exception")
    public String exp() {

        log.info("Inside ExceptionController Controller ...");

        int i=0;
        if (i == 0)
            throw new MyException("Some error took place, just test Exception Advice");

        return "Never returned String";
    }
}
