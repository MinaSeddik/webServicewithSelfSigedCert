package com.example.springbootproject.service;

import com.example.springbootproject.exception.InvalidUserException;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class OrderCreationService {

    private final AtomicLong orderTotalCountSinceUptime = new AtomicLong(0);
    private final AtomicLong orderPassCountSinceUptime = new AtomicLong(0);
    private final AtomicLong orderFailCountSinceUptime = new AtomicLong(0);

    private final AtomicReference<LocalDateTime> lastSucceededOrderDate = new AtomicReference<LocalDateTime>();
    private final AtomicReference<LocalDateTime> lastFailedOrderDate = new AtomicReference<LocalDateTime>();
    private final AtomicReference<String> lastFailedOrderReason = new AtomicReference<String>();
    private final AtomicReference<FailureInfo> lastFailedOrderInfo = new AtomicReference<FailureInfo>();


    public void createOrder() {

        orderTotalCountSinceUptime.getAndIncrement();

        try {
            internalBusinessLogicAndPayment();
        } catch (Exception ex) {
            orderFailCountSinceUptime.getAndIncrement();
            lastFailedOrderDate.set(LocalDateTime.now());
            lastFailedOrderReason.set(LocalDate.now() + " " + ex.getMessage());

            lastFailedOrderInfo.set(FailureInfo.builder()
                    .date(LocalDateTime.now())
                    .exception(ex.getClass())
                    .message(ex.getMessage())
                    .build());


//            log.error("log error and exception info ....");
        }

        lastSucceededOrderDate.set(LocalDateTime.now());
        orderPassCountSinceUptime.getAndIncrement();
    }

    private void internalBusinessLogicAndPayment() throws Exception {

        throw new InvalidUserException("Invalid payment card");
    }

    public FailureInfo getLastFailureInfo(){
        return lastFailedOrderInfo.get();
    }

    @Data
    @Builder
    public static class FailureInfo {
        private LocalDateTime date;
        private Class<?> exception;
        private String message;
    }
}
