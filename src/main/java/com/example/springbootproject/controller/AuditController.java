package com.example.springbootproject.controller;

import com.example.springbootproject.audit.Event;
import com.example.springbootproject.audit.EventAction;
import com.example.springbootproject.audit.EventLoggingService;
import com.example.springbootproject.audit.event.OrderCreationSuccessEventAction;
import com.example.springbootproject.domain.Order;
import com.example.springbootproject.domain.OrderType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
public class AuditController {

    @Autowired
    private EventLoggingService eventLoggingService;

    @GetMapping(value = "/audit")
    public List<Event> getAllAuditRecords() {

        log.info("Fetch All audit records ...");

        List<Event> events = eventLoggingService.getAllEvents();

        log.info("Received {} event(s)", events.size());

        return events;
    }

}
