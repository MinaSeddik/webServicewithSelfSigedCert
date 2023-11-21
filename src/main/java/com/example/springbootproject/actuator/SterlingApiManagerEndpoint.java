package com.example.springbootproject.actuator;

import com.example.springbootproject.actuator.model.MyAppCacheInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;


// to enable this endpoint, you should out it into management.endpoints.web.exposure.include in application.properties


@Component
@Endpoint(id = "sterling-api-manager")
@Slf4j
public class SterlingApiManagerEndpoint {

    /*
    GET
    http://localhost:8080/actuator/sterling-api-manager
 */
    @ReadOperation
    public List<String> getAllSterlingOrdersInfo() {



        return Arrays.asList("Please implement me like CacheManagerEndpoint class and PaymentHealthIndicator class!");
    }
}
