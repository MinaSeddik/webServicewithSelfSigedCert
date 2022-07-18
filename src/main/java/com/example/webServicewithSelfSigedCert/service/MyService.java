package com.example.webServicewithSelfSigedCert.service;


import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyService {

    @Autowired
    public MeterRegistry meterRegistry;


    @Timed(value = "my-app.my-service.userinfo")
    public int doThis(){


        return 10;
    }

    public int doThat(){

        meterRegistry.gauge("my-app.my-service.xxxxx", 236.56);

        Gauge gauge = Gauge
                .builder("my-app.my-service.yyyyy", () -> 56666666)
                .tag("test1", "XXXSWEDCDD")
                .register(meterRegistry);

        return 10;
    }
}
