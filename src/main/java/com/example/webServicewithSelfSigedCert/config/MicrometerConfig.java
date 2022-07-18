package com.example.webServicewithSelfSigedCert.config;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MicrometerConfig {

    @Bean
    public CountedAspect countedAspect(MeterRegistry registry) {
        return new CountedAspect(registry);
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

//    @Bean
//    public MeterRegistryCustomizer meterRegistryCustomizer(MeterRegistry meterRegistry) {
//        return meterRegistry1 -> {
//            meterRegistry.config().commonTags("application", "my-application");
//        };
//    }
}
