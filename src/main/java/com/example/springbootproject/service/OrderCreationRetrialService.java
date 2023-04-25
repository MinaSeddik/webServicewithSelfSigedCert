package com.example.springbootproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderCreationRetrialService {

    ThreadFactory threadFactory = new ThreadFactory() {

        private int counter = 0;
        private String prefix = "order-retrial";

        @Override
        public Thread newThread(Runnable runnable) {
            Thread t = new Thread(runnable, prefix + "-" + counter++);

            t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    log.error("");
                }
            });

            return t;
        }
    };


    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, threadFactory);

    public void scheduleRetrial(Runnable task, int attempt) {


        /**
         *
         * We can apply the Retry Policy as needed
         *
         */
//        int delayInHours = (int) Math.pow(2, attempt);
        int delayInMinutes = (int) Math.pow(2, attempt);

        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.systemDefault();
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
//        ZonedDateTime zonedNextTarget = zonedNow.plusHours(delayInHours);
        ZonedDateTime zonedNextTarget = zonedNow.plusMinutes(delayInMinutes);
        Duration duration = Duration.between(zonedNow, zonedNextTarget);

        executorService.schedule(task, duration.getSeconds(), TimeUnit.SECONDS);

    }


    public void stop() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            log.error("");
        }
    }

}
