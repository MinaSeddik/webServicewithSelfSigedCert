package com.example.springbootproject.audit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Device {

    private static final String EMPTY_STRING = "";
    public static final Device DEVICE_NULL = Device.builder()
            .hostname(EMPTY_STRING)
            .hostAddress(EMPTY_STRING)
            .macAddress(EMPTY_STRING)
            .osUser(EMPTY_STRING)
            .build();

    private String hostname;
    private String hostAddress;
    private String macAddress;
    private String osUser;

}
