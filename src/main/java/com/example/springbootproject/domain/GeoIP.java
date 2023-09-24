package com.example.springbootproject.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GeoIP {
    private String ipAddress;
    private String city;
    private String latitude;
    private String longitude;
}