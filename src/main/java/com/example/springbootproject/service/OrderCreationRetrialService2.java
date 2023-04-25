package com.example.springbootproject.service;

import com.example.springbootproject.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
@Slf4j
public class OrderCreationRetrialService2 {

    ThreadFactory threadFactory = new ThreadFactory() {

        private int counter = 0;
        private String prefix = "order-retrial2";

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

    private final Runnable FAILBACK_NOTHING = () -> {

    };

    private ThreadLocal threadLocal = new ThreadLocal<>();
    private ExecutorService executorService = Executors.newCachedThreadPool(threadFactory);


    public void scheduleRetrial(Runnable task) {
        scheduleRetrial(task, FAILBACK_NOTHING);
    }

    public void scheduleRetrial(Runnable task, Runnable recover) {


        // should raise exception MyException, for retrial
        doScheduleRetrial(task, recover);

    }


    @Retryable(include = {MyException.class}, maxAttempts = 5,
            backoff = @Backoff(delay = 60000L, maxDelay = 6000000L, multiplier = 2))
    public void doScheduleRetrial(Runnable task, Runnable recover) {


        CompletableFuture.runAsync(() -> {
            try {
                task.run();
            } catch (MyException e) {
                log.info("runAsync MyException raised.");
                throw e;
            }
        }, executorService);



        // should raise exception MyException, for retrial
//        executorService.submit(() -> {
//
//            threadLocal.set(recover);
//
//            try{
//
//                task.call();
//                log.info("YYYYYYYYYYYYYYYY");
//            }catch (MyException ex){
//                log.info("-----------------");
//                throw new MyException(ex.getCause().getMessage());
//            } catch (Exception e) {
//                log.info("####################");
//                throw new RuntimeException(e);
//            }
//
//        });

//        try {
//            future.();
//        } catch (ExecutionException ex) {
////            log.info("----- " + ex.getCause());
//
//            if (ex.getCause() instanceof MyException) {
//
//            }
//
////            ex.getCause().printStackTrace();
//        } catch (InterruptedException e) {
////            log.info("****** " + e.getCause());
////            throw new RuntimeException(e);
//        }

    }


    @Retryable(include = {MyException.class}, maxAttempts = 5,
            backoff = @Backoff(delay = 60000L, maxDelay = 6000000L, multiplier = 2))
    public void scheduleRetrialWithRecover(Runnable task) {

        // should raise exception MyException, for retrial
        Future<?> future = executorService.submit(task);

    }

//    @Recover
//    public void recover(MyException e, Order sql) throws SQLException {
//
//        log.info("Calling recover ...");
////        throw e;
//    }


    public void stop() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            log.error("");
        }
    }

}
