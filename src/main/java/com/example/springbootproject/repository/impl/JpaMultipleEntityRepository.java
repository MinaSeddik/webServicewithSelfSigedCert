package com.example.springbootproject.repository.impl;

import com.example.springbootproject.domain.Person2;
import org.springframework.context.annotation.Profile;
//import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Profile("spring-data-jpa")
@Repository
public interface JpaMultipleEntityRepository
//        extends  /* implements multiple interfaces*/
//        JpaRepository<Person2, Integer>,
//        JpaPerson2Repository,
//        JpaPersonRepository
{
}
