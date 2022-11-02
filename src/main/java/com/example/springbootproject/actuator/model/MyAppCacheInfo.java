package com.example.springbootproject.actuator.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MyAppCacheInfo {

    private String cacheName;
    private int cacheSize;

}
