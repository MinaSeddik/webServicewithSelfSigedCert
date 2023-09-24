package com.example.springbootproject.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseMessagingConfig {

    @Value("${app.firebase-configuration-file}")
    private String firebaseConfigPath;

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    @Bean
    public FirebaseApp firebaseApp(GoogleCredentials googleCredentials) {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        return GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream());
    }
}
