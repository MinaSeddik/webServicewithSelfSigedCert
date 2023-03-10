package com.example.springbootproject.controller;

import com.example.springbootproject.domain.BigRecord;
import com.example.springbootproject.service.ReactiveBigDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@Slf4j
public class ReactiveBigDataController {

    @Autowired
    private ReactiveBigDataService reactiveBigDataService;


    @RequestMapping(value = "/all-data", method = RequestMethod.GET,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//            produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<BigRecord> fetchAllData() {

        return reactiveBigDataService.fetchAllRecords();
//                .doOnNext(d -> log.info("ReactiveBigDataController : {}", d));

    }

}
