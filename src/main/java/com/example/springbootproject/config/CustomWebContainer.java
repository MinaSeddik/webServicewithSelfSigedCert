package com.example.springbootproject.config;

import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("web-container")
@Configuration
public class CustomWebContainer {
    public void customize(ConfigurableServletWebServerFactory factory){
        factory.setPort(8042);
    }
}