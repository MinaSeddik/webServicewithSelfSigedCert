package com.example.springbootproject.controller;


import io.micrometer.core.annotation.Timed;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.concurrent.*;

@RestController
@Timed
public class MyAsyncController {

    ExecutorService executor = Executors.newFixedThreadPool(10);

    @RequestMapping(value = "/async")
    public void asyncGet(@Suspended final AsyncResponse asyncResponse) {

        System.out.println("*** asyncGet API Started! Thread Name: " + Thread.currentThread().getName());

        executor.execute(() -> {
            String result = veryExpensiveOperation();
            asyncResponse.resume(result);
        });

        System.out.println("*** asyncGet API Completed! Thread Name: " + Thread.currentThread().getName());

    }

    private static final BlockingQueue<AsyncResponse> suspended =
            new ArrayBlockingQueue<AsyncResponse>(5);

    @RequestMapping(value = "/async2")
    public void asyncGet2() throws InterruptedException {
        System.out.println("*** asyncGet2 API Started! Thread Name: " + Thread.currentThread().getName());


//        final AsyncResponse ar = suspended.take();


        executor.execute(() -> {
            String result = veryExpensiveOperation();
//            ar.resume(result);
        });

        System.out.println("*** asyncGet2 API Completed! Thread Name: " + Thread.currentThread().getName());


    }

    @RequestMapping(value = "/async3")
    public CompletionStage<String> asyncGet3() {

        System.out.println("*** asyncGet3 API Started! Thread Name: " + Thread.currentThread().getName());

        Future<String> future2 = executor.submit(() -> veryExpensiveOperation());

        CompletableFuture<String> future = new CompletableFuture<>();
        executor.execute(() -> {
            String result = veryExpensiveOperation2(future);
        });

        System.out.println("*** asyncGet3 API Completed! Thread Name: " + Thread.currentThread().getName());


        return future;
    }

    private String veryExpensiveOperation() {

        try {
            Long duration = (long) (25);
            System.out.println("veryExpensiveOperation Task Started! Thread Name: " + Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(duration);
            System.out.println("veryExpensiveOperation Completed! Thread Name: " + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        String result = "Hello from the other side ...";
        return result;
    }

    private String veryExpensiveOperation2(CompletableFuture<String> future) {

        try {
            Long duration = (long) (80);
            System.out.println("veryExpensiveOperation Task Started! Thread Name: " + Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(duration);
            System.out.println("veryExpensiveOperation Completed! Thread Name: " + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        String result = "Hello from the other side ...";
        future.complete(result);
        return result;
    }
}
