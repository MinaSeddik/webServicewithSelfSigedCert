package com.example.springbootproject.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class RobustWithCircuitBreakerService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RetryTemplate retryTemplate;

    public String getExternalCustomerName() {

        log.info("Call getExternalCustomerName ...");

        ResponseEntity<String> exchange = restTemplate.exchange("http://localhost:8080/api/customer/name",
                HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
                });
        return exchange.getBody();
    }

    @CircuitBreaker(maxAttempts = 3, openTimeout = 5000L, resetTimeout = 15000L)
    public String resilientCustomerName() {

        log.info("Call resilientCustomerName ...");
        return getExternalCustomerName();

//        return retryTemplate.execute(new RetryCallback<String, RuntimeException>() {
//            @Override
//            public String doWithRetry(RetryContext context) {
//                log.info(String.format("Retry count %d", context.getRetryCount()));
//                return getExternalCustomerName();
//
//            }
//        });
    }

    @Recover
    public String fallback(Throwable e) {
        log.info("returning name from fallback method");
        return "Mini";
    }

}
