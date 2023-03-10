package com.example.springbootproject.service;

import com.example.springbootproject.domain.BigRecord;
import com.example.springbootproject.repository.impl.ReactiveBigDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class ReactiveBigDataService {

    @Autowired
    private ReactiveBigDataRepository reactiveBigDataRepository;


    public Flux<BigRecord> fetchAllRecords() {

        return reactiveBigDataRepository.fetchAllRecords();
//                .doOnNext(a -> log.info("ReactiveAtmService::fetchAllRecords {}", a));
    }


}
