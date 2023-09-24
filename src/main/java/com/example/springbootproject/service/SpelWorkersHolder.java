package com.example.springbootproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component("workersHolder")
@Slf4j
public class SpelWorkersHolder {

    private List<String> workers = new LinkedList<>();
    private Map<String, Integer> salaryByWorkers = new HashMap<>();

    @Autowired
    public SpelWorkersHolder() {
        workers.add("John");
        workers.add("Susie");
        workers.add("Alex");
        workers.add("George");

        salaryByWorkers.put("John", 35000);
        salaryByWorkers.put("Susie", 47000);
        salaryByWorkers.put("Alex", 12000);
        salaryByWorkers.put("George", 14000);
    }

    //Getters and setters
}