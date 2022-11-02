package com.example.springbootproject.service;


import com.example.springbootproject.repository.MyTestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class MyRepoService {

    @Autowired
    public MyTestRepository myTestRepository;

    public String getAllItems() {
        int size = myTestRepository.getAllItems();
//        log.debug("some string .. {} and {}", size, myTestRepository.toString());
        return "table size = " + size;
    }

    @Transactional
    public String trans1() {
        int size = myTestRepository.getAllItems();
        return "trans1 size = " + size;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String trans2() {
        int size = myTestRepository.getAllItems();
        return "trans2 size = " + size;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String trans3() {
        int size = myTestRepository.getAllItems();
        return "trans3 size = " + size;
    }

}
