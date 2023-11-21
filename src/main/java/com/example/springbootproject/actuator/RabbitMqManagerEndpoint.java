package com.example.springbootproject.actuator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


// to enable this endpoint, you should out it into management.endpoints.web.exposure.include in application.properties

@Component
@Endpoint(id = "rabbitmq-manager")
@Slf4j
public class RabbitMqManagerEndpoint {

//    Use //http://localhost:15672/api/index.html to call rabbit api to check the system status

    /*
GET
http://localhost:8080/actuator/rabbitmq-manager
*/
    @ReadOperation
    public List<String> getRabbitMqInfo() {



        return Arrays.asList("Please implement me like CacheManagerEndpoint class and PaymentHealthIndicator class!");
    }

}
