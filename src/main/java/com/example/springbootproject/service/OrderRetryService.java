package com.example.springbootproject.service;

import com.example.springbootproject.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;
import com.example.springbootproject.retrial.SimpleOrderCreationJob;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.example.springbootproject.util.Constants.ORDER_ID;
import static com.example.springbootproject.util.Constants.RETRIAL_NUMBER;
import static org.quartz.JobBuilder.newJob;

@Service
@Slf4j
public class OrderRetryService {

    private Scheduler scheduler;

    @PostConstruct
    private void initScheduler() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
        scheduler.start();
    }


    public void createOrder(Order order) throws SchedulerException {

        // normal steps to create the order
        // ...

        // assume that the order failed due to specific known Exception

        // handle retrial policy
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("order", order);

        log.info("Prepare JobDetail ...");
        JobDetail job = newJob(SimpleOrderCreationJob.class)
                .withIdentity("OrderRetrialJob:" + order.getOrderId() + ":1")
                .usingJobData(jobDataMap)
                .usingJobData(ORDER_ID, order.getOrderId())
                .usingJobData(RETRIAL_NUMBER, 1)
                .build();

        log.info("Current Time: {}", LocalDateTime.now());

        LocalDateTime nowPlusAnHour = LocalDateTime.now().plusMinutes(3L);

//        LocalDateTime nowPlusAnHour = LocalDateTime.now().plusHours(1L);
        log.info("Next Retrial Time: {}", nowPlusAnHour);

        Date nextRetrialDate = Date.from(nowPlusAnHour.atZone(ZoneId.systemDefault()).toInstant());
        log.info("Next Retrial Date: {}", nextRetrialDate);

        log.info("Prepare a trigger ...");
//        Build a trigger for a specific moment in time, with no repeats:
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("OrderRetrialTrigger:" + order.getOrderId() + ":1")
                .startAt(nextRetrialDate)
                .forJob(job)
                .build();

        log.info("Schedule the trigger ...");
        scheduler.scheduleJob(job, trigger);

    }


}
