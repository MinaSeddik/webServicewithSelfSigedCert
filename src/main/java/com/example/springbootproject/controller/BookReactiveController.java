package com.example.springbootproject.controller;

import com.example.springbootproject.model.Book;
import com.example.springbootproject.service.BookReactiveService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@Timed
public class BookReactiveController {

    @Autowired
    private BookReactiveService bookReactiveService;


    @RequestMapping(value = "/book",
            method = RequestMethod.GET,
//            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Book> book1() {

        return bookReactiveService.dosomething()
                .doOnNext(b -> log.info("Book controller: {}", b));

    }

    @RequestMapping(value = "/book2",
            method = RequestMethod.GET,
//            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Book> book2() {

        return bookReactiveService.dosomethingAsFlux()
                .doOnNext(b -> log.info("Book controller: {}", b));

    }

    @RequestMapping(value = "/book3",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//            produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Book> book3() {

        return bookReactiveService.dosomethingAsFlux()
                .doOnNext(b -> log.info("Book controller: {}", b));

    }
}
