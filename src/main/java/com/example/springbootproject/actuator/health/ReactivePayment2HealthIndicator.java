package com.example.springbootproject.actuator.health;

import com.example.springbootproject.service.OrderCreationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;

@Component("payment-reactive")  // to change the url name
public class ReactivePayment2HealthIndicator implements ReactiveHealthIndicator {

    @Autowired
    private OrderCreationService orderCreationService;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS");

    @PostConstruct
    public void init() {
        // for testing only
        orderCreationService.createOrder();
    }


    @Override
    public Mono<Health> health() {

//        double chance = ThreadLocalRandom.current().nextDouble();

        // make it fail for testing
//        double chance = 1.3;
        double chance = 0.5;

        return Mono.fromCallable(() -> orderCreationService.getLastFailureInfo())
                .map(failureInfo -> {
                    if (chance < 0.9) {
                        return new Health.Builder()
                                .up()
//                                .withDetail("level", level)
                                .build();
                    } else {
                        return Health.down()
                                .withDetail("Date", failureInfo.getDate().format(formatter))
                                .withDetail("Exception", failureInfo.getException())
                                .withDetail("Message", failureInfo.getMessage())
                                .build();
                    }
                }).onErrorResume(err -> Mono.just(new Health.Builder()
                        .outOfService()
                        .withDetail("error", err.getMessage())
                        .build())
                );

    }

}