package com.example.springbootproject.actuator.health;

import com.example.springbootproject.service.OrderCreationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;

@Component
public class PaymentHealthIndicator implements HealthIndicator {

    @Autowired
    private OrderCreationService orderCreationService;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS");

    @PostConstruct
    public void init() {
        // for testing only
        orderCreationService.createOrder();
    }

    @Override
    public Health health() {

        OrderCreationService.FailureInfo failureInfo = orderCreationService.getLastFailureInfo();
//        double chance = ThreadLocalRandom.current().nextDouble();

        // make it fail for testing
        double chance = 0.5;

        Health.Builder status = Health.up();
        if (chance > 0.9) {
            status = Health.down()
                    .withDetail("Date", failureInfo.getDate().format(formatter))
                    .withDetail("Exception", failureInfo.getException())
                    .withDetail("Message", failureInfo.getMessage());
        }

        return status.build();
    }

}