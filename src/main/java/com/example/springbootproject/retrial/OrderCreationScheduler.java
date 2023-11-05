package com.example.springbootproject.retrial;

import com.example.springbootproject.SpringbootProjectApplication;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.example.springbootproject.util.Constants.ORDER_ID;
import static com.example.springbootproject.util.Constants.RETRIAL_NUMBER;
import static org.quartz.JobBuilder.newJob;

@Slf4j
@Component
public class OrderCreationScheduler {

    @Autowired
    private Scheduler scheduler;

    public void scheduleOrderCreationRetry(int orderId, int retrialNumber) throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
//        jobDataMap.put("order", order);

        log.info("Prepare JobDetail ...");
        JobDetail job = newJob(SimpleOrderCreationJob.class)
                .withIdentity("OrderRetrialJob:" + orderId + ":" + retrialNumber)
                .usingJobData(jobDataMap)
                .usingJobData(ORDER_ID, orderId)
                .usingJobData(RETRIAL_NUMBER, retrialNumber)
                .build();

        log.info("Current Time: {}", LocalDateTime.now());

        long waitingHours = (long) Math.pow(2, retrialNumber - 1);

        LocalDateTime nowPlusAnHour = LocalDateTime.now().plusMinutes(waitingHours);

//        LocalDateTime nowPlusAnHour = LocalDateTime.now().plusHours(waitingHours);
        log.info("Next Retrial Time: {}", nowPlusAnHour);

        Date nextRetrialDate = Date.from(nowPlusAnHour.atZone(ZoneId.systemDefault()).toInstant());
        log.info("Next Retrial Date: {}", nextRetrialDate);

        log.info("Prepare a trigger ...");
//        Build a trigger for a specific moment in time, with no repeats:
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("OrderRetrialTrigger:" + orderId + ":" + retrialNumber)
                .startAt(nextRetrialDate)
                .forJob(job)
                .build();

        log.info("Schedule the trigger ...");
        scheduler.scheduleJob(job, trigger);
    }

}
