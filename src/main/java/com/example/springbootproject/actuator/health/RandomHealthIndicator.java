package com.example.springbootproject.actuator.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
//        double chance = ThreadLocalRandom.current().nextDouble();
        double chance = 0.5;
        Health.Builder status = Health.up();
        if (chance > 0.9) {
            status = Health.down();
        }
        return status.build();
    }

}