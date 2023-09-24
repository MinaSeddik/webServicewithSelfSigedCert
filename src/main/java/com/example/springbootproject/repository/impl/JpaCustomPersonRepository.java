package com.example.springbootproject.repository.impl;

import com.example.springbootproject.domain.Person2;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface JpaCustomPersonRepository {
    List<Person2> findUserByEmails(Set<String> emails);

    List<Person2> findWhatever(Set<String> params);
}
