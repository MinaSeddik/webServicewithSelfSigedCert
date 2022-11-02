package com.example.springbootproject.service;

import com.example.springbootproject.model.BigRecord;
import com.example.springbootproject.repository.ImperativeBigDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ImperativeBigDataService {

    @Autowired
    private ImperativeBigDataRepository imperativeBigDataRepository;

    public List<BigRecord> fetchAllRecords() {

        List<BigRecord> list = imperativeBigDataRepository.fetchAllRecords();
        log.info("Total count: {}", list.size());

        return list;
    }
}
