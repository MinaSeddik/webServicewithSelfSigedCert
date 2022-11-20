package com.example.springbootproject.service;


import com.example.springbootproject.repository.impl.MyTestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class MyRepoService {

    @Autowired
    public MyTestRepository myTestRepository;

    public Map<String, String> getAllItems() {
        int size = myTestRepository.getAllItems();

        Map<String, String> map = new HashMap();
        map.put("table size", Integer.toString(size));

        SecurityContext securityContext = SecurityContextHolder.getContext();
        if(!Objects.isNull(securityContext)){
            String username = securityContext.getAuthentication().getName();
            map.put("user_for service layer", username);
        }

        return map;
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
