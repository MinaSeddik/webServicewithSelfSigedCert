package com.example.springbootproject.audit;


//Reference: https://github.com/gchq/event-logging

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventLoggingService {
    void logEvent(EventAction eventAction);
    List<Event> getAllEvents();
}
