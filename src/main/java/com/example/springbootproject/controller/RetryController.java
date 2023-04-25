package com.example.springbootproject.controller;

import com.example.springbootproject.service.RobustWithCircuitBreakerService;
import com.example.springbootproject.service.RobustWithRetractableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@Slf4j
public class RetryController {

    @Autowired
    private RobustWithRetractableService retryService;

    @Autowired
    private RobustWithCircuitBreakerService retryService2;


    @RequestMapping(value = "/retry")
    public void retry() throws SQLException {

        retryService.retryServiceWithRecovery("sample sql");
//        retryService.retryServiceWithCustomization("sample sql");
//        retryService.retryServiceWithExternalConfiguration("sample sql");
//        retryService.retryMethodWithoutRandomDelay("sample sql");
//        retryService.retryMethodWitRandomDelay("sample sql");
    }

    @RequestMapping(value = "/retry2")
    public String retry2() throws RuntimeException {

        return retryService2.resilientCustomerName();
    }

}
