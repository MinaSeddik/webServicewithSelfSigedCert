package com.example.springbootproject.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Profile("jdbc-security")
@Configuration
public class JdbcUserDetailsManagerConfig {

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
//        return new JdbcUserDetailsManager(dataSource);

        String usersByUsernameQuery =
                "select username, password, enabled from users where username = ?";
        String authsByUserQuery =
                "select username, authority from authorities where username = ?";

        var userDetailsManager = new JdbcUserDetailsManager(dataSource);
        userDetailsManager.setUsersByUsernameQuery(usersByUsernameQuery);
        userDetailsManager.setAuthoritiesByUsernameQuery(authsByUserQuery);
        return userDetailsManager;

    }

    @Bean
    @Profile("p0")
    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//        return new BCryptPasswordEncoder(4);

        SecureRandom secureRandom = null;
        try {
            secureRandom = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

//        2^log rounds = 2^10
//        allowed log rounds is 4 to 31.
        return new BCryptPasswordEncoder(10, secureRandom);
    }

    @Bean
//    @Profile("p1")
    public PasswordEncoder plainPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    @Profile("p2")
    public PasswordEncoder standardPasswordEncoder() {
        return new StandardPasswordEncoder("secret");
    }

    @Bean
    @Profile("p3")
    public PasswordEncoder pbkdf2PasswordEncoder() {
//        return new Pbkdf2PasswordEncoder("secret");

//        performs an HMAC as many times as specified by an iterations argument.
        return new Pbkdf2PasswordEncoder("secret", 185000, 256);
    }

    @Bean
    @Profile("p4")
    public PasswordEncoder sCryptPasswordEncoder() {
//        return new SCryptPasswordEncoder();
        return new SCryptPasswordEncoder(16384, 8, 1, 32, 64);
    }
}
