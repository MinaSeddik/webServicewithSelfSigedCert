package com.example.springbootproject.config;

import com.example.springbootproject.retrial.SimpleOrderCreationJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Properties;

@Configuration
public class QuartzSchedulerConfig {
//
//    @Bean
//    public JobDetail jobDetail() {
//        return JobBuilder.newJob().ofType(SimpleOrderCreationJob.class)
//                .withIdentity("Qrtz_Job_Detail")
//                .withDescription("Invoke Sample Job service...")
//                .build();
//
//        //        JobDetail job = newJob(SimpleOrderCreationJob.class)
////                .withIdentity("OrderRetrialJob:" + orderId + ":" + retrialNumber)
////                .usingJobData(jobDataMap)
////                .usingJobData(ORDER_ID, orderId)
////                .usingJobData(RETRIAL_NUMBER, retrialNumber)
////                .build();
//    }

//    @Bean
//    public JobDetailFactoryBean jobDetail() {
//        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
//        jobDetailFactory.setJobClass(SimpleOrderCreationJob.class);
//        jobDetailFactory.setDescription("Invoke Sample Job service...");
//        jobDetailFactory.setDurability(true);
//        return jobDetailFactory;
//    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory) throws SchedulerException {
        Scheduler scheduler = factory.getScheduler();
        scheduler.start();
        return scheduler;
    }

}
