package com.example.springbootproject.controller;

import com.example.springbootproject.model.BigRecord;
import com.example.springbootproject.service.ImperativeBigDataService;
import com.example.springbootproject.service.ReactiveBigDataService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@Slf4j
@Timed
public class ImperativeBigDataController {

    @Autowired
    private ImperativeBigDataService imperativeBigDataService;

    @RequestMapping(value = "/all-data-x", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BigRecord> fetchAllData() {

        return imperativeBigDataService.fetchAllRecords();

    }

}
