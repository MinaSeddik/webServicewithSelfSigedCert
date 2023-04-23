package com.example.springbootproject.audit;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventLoggingRepository {
    void save(Event event);
    List<Event> getAllRecords();
}
