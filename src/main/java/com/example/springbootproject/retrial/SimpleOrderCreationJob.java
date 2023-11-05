package com.example.springbootproject.retrial;

import com.example.springbootproject.SpringbootProjectApplication;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;

import static com.example.springbootproject.util.Constants.ORDER_ID;
import static com.example.springbootproject.util.Constants.RETRIAL_NUMBER;

@Slf4j
public class SimpleOrderCreationJob implements Job {

    private static final int MAX_RETRY_COUNT = 5;

    //    @Autowired
    private OrderCreationScheduler orderCreationScheduler;

    public SimpleOrderCreationJob() {
        ApplicationContext context = SpringbootProjectApplication.getApplicationContext();
        orderCreationScheduler = context.getBean(OrderCreationScheduler.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("========= START JOB EXECUTION =============");

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        int orderId = dataMap.getIntValue(ORDER_ID);
        int retrialNumber = dataMap.getIntValue(RETRIAL_NUMBER);

        log.info("Order Id: {} - Retrial Number: {}", orderId, retrialNumber);

        // retry create the order
        log.info("===>>> Retry #{} - RUN AT: {}", retrialNumber, LocalDateTime.now());

        log.info("========= END JOB EXECUTION =============");


        // Now assume that the retrial failed
        if (++retrialNumber > MAX_RETRY_COUNT)
            return;

        try {
            orderCreationScheduler.scheduleOrderCreationRetry(orderId, retrialNumber);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }


    }
}