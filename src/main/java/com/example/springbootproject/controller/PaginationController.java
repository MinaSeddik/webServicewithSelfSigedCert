package com.example.springbootproject.controller;

import com.example.springbootproject.domain.Book;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PaginationController {


    @GetMapping("/filter")
    public Page<Book> filterBooks(@ParameterObject Pageable pageable) {


//        return repository.getBooks(pageable);

        return null;
    }

}
