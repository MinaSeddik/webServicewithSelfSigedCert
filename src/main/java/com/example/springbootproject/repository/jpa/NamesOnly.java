package com.example.springbootproject.repository.jpa;

import org.springframework.beans.factory.annotation.Value;

public interface NamesOnly {
    String getFirstName();
    String getLastName();

    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName();
}
