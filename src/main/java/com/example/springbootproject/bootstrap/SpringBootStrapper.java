package com.example.springbootproject.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpringBootStrapper {

    @Async
    @EventListener
    public void handleContextStart(ContextRefreshedEvent contextRefreshedEvent) {

        log.info("*********************** Application started ***********************");

    }


}
