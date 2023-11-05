package com.example.springbootproject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

//@SpringBootApplication(exclude = {
//		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
//		org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class}
//)
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class,

        // disable R2dbc transaction
        R2dbcTransactionManagerAutoConfiguration.class})
@Slf4j
public class SpringbootProjectApplication implements CommandLineRunner {

//	–spring.config.location=file://{path to file}.

    public static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "false");

        SpringApplication application = new SpringApplication(SpringbootProjectApplication.class);
        application.addListeners(new ApplicationPidFileWriter());


//        https://stackoverflow.com/questions/31619383/applicationpidfilewriter-doesnt-produce-pid-file-on-spring-boot
//        ConfigurableApplicationContext applicationContext = application.run(args);
         applicationContext = application.run(args);
//		ConfigurableApplicationContext applicationContext = SpringApplication.run(SpringbootProjectApplication.class, args);
//		applicationContext.start();
//		applicationContext.stop();
//		applicationContext.refresh();
//		applicationContext.close();
    }


    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    @Override
    public void run(String... args) {
        log.info("EXECUTING : command line runner");

        for (int i = 0; i < args.length; ++i) {
            log.info("args[{}]: {}", i, args[i]);
        }


        // log4j CVE-2021-44228 - fixed in 2.15.0

//        String data = "${env:java.version}";  //env lookup
        String data = "${java:runtime}";  //env lookup
//        String data = "${env:HOME}";  // env lookup
//        String data = "${jndi:ldap//}"; // jndi lookup

        log.info("here is a sample log: " + data);
        log.info("here is a sample log: {}", data);


    }
}
