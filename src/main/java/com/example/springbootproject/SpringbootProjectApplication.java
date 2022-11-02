package com.example.springbootproject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@Slf4j
public class SpringbootProjectApplication implements CommandLineRunner {

	public static void main(String[] args) {
		System.setProperty("spring.devtools.restart.enabled", "false");
		ConfigurableApplicationContext applicationContext = SpringApplication.run(SpringbootProjectApplication.class, args);

//		applicationContext.start();
//		applicationContext.stop();
//		applicationContext.refresh();
//		applicationContext.close();
	}

	@Override
	public void run(String... args) {
		log.info("EXECUTING : command line runner");

		for (int i = 0; i < args.length; ++i) {
			log.info("args[{}]: {}", i, args[i]);
		}
	}
}
