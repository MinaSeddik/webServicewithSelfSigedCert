package com.example.springbootproject.audit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class EventDetail {

    private EventAction eventAction;
}
