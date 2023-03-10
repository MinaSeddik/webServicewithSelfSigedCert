package com.example.springbootproject.security;

import com.example.springbootproject.domain.InMemoryUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Profile("test2")
@Configuration
public class InMemoryUserDetailsServiceConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails u = new InMemoryUser("john", "12345", "READ");
        List<UserDetails> users = List.of(u);


        return new InMemoryUserDetailsService(users);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
