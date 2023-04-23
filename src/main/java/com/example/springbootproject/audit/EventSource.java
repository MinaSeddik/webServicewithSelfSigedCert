package com.example.springbootproject.audit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class EventSource {

    private SystemDetail systemDetail;
    private Device device;
    private Client client;
    private UserRequestInfo user;

}
