package com.example.springbootproject.audit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

//Reference: https://github.com/gchq/event-logging

@Builder
@Getter
@Setter
public class Event {

    // When
    private EventTime eventTime;

    // Who
    private EventSource eventSource;

    // What
    private EventDetail eventDetail;


}