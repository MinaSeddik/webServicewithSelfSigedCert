package com.example.springbootproject.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig {

//    @Autowired
//    private TickService tickService;
//
//    @Bean
//    public Executor taskExecutor() {
//        return Executors.newSingleThreadScheduledExecutor();
//    }

//    @Override
//    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
//        taskRegistrar.setScheduler(taskExecutor());
//        taskRegistrar.addTriggerTask(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        tickService.tick();
//                    }
//                },
//                new Trigger() {
//                    @Override
//                    public Date nextExecutionTime(TriggerContext context) {
//                        Optional<Date> lastCompletionTime =
//                                Optional.ofNullable(context.lastCompletionTime());
//                        Instant nextExecutionTime =
//                                lastCompletionTime.orElseGet(Date::new).toInstant()
//                                        .plusMillis(tickService.getDelay());
//                        return Date.from(nextExecutionTime);
//                    }
//                }
//        );
//    }
//
}
