package com.example.springbootproject.service;

import com.example.springbootproject.domain.Person2;
import com.example.springbootproject.repository.impl.JpaCustomPersonRepository;
//import com.example.springbootproject.repository.impl.JpaPerson2Repository;
import com.example.springbootproject.repository.impl.JpaPersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class JpaPersonService {

//    @Autowired
//    private JpaPerson2Repository jpaPerson2Repository;

//    @Autowired
//    private JpaCustomPersonRepository jpaCustomPersonRepository;

    public Iterable<Person2> getPersons() {

        log.info("inside JpaPersonService::getPersons");
//        return jpaPerson2Repository.findAll();

        return null;
    }

    public Optional<Person2> getUSer(String firstName) {

        log.info("inside JpaPersonService::getUSer: {}", firstName);

//        return jpaPerson2Repository.findByFirstName(firstName);
        return null;
    }
}
