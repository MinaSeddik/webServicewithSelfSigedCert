package com.example.springbootproject.repository.impl;

import com.example.springbootproject.domain.Person2;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaPerson2Repository extends CrudRepository<Person2, Integer> {
    Optional<Person2> findByFirstName(String firstName);
}
