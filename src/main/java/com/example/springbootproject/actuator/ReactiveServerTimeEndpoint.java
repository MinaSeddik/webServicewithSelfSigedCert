package com.example.springbootproject.actuator;

import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

// to enable this endpoint, you should out it into management.endpoints.web.exposure.include in application.properties


public class ReactiveServerTimeEndpoint {
    private Mono<Long> getNtpTimeOffset() { // (2)
        // Actual network call to get the current time offset

        // mock the call
        return Mono.just(Instant.now().getEpochSecond());
    }

    @ReadOperation // (3)
    public Mono<Map<String, Object>> reportServerTime() { // (4)
        return getNtpTimeOffset() // (5)
                .map(timeOffset -> { // (6)
                    Map<String, Object> rsp = new LinkedHashMap<>(); //
                    rsp.put("serverTime", Instant.now().toString()); //
                    rsp.put("ntpOffsetMillis", timeOffset); //
                    return rsp;
                });
    }
}
