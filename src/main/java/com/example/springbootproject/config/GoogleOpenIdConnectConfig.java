package com.example.springbootproject.config;

import com.example.springbootproject.domain.Credential;
import com.example.springbootproject.security.GoogleUserInfoTokenServices;
import com.example.springbootproject.security.LoggingInterceptor;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

@Profile("oauth2-resttemplete")
@Configuration
@EnableOAuth2Client
public class GoogleOpenIdConnectConfig {
    @Value("${google.clientId}")
    private String clientId;

    @Value("${google.clientSecret}")
    private String clientSecret;

    @Value("${google.accessTokenUri}")
    private String accessTokenUri;

    @Value("${google.userAuthorizationUri}")
    private String userAuthorizationUri;

    @Value("${google.redirectUri}")
    private String redirectUri;

    @Value("${google.userInfoUri}")
    private String userInfoUri;

    @Autowired
    private LoggingInterceptor loggingInterceptor;

    @Bean
    public OAuth2ProtectedResourceDetails googleOpenId() {
        final AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        details.setClientId(clientId);
        details.setClientSecret(clientSecret);
        details.setAccessTokenUri(accessTokenUri);
        details.setUserAuthorizationUri(userAuthorizationUri);
        details.setScope(Arrays.asList("openid",
                "email",
                "profile",
                "https://www.googleapis.com/auth/drive"));

        details.setPreEstablishedRedirectUri(redirectUri);
        details.setUseCurrentUri(false);

//        details.setAuthenticationScheme(AuthenticationScheme.query);
//        details.setClientAuthenticationScheme(AuthenticationScheme.form);

        return details;
    }

    @Bean
    public OAuth2RestTemplate googleOpenIdTemplate(final OAuth2ClientContext clientContext) {
        final OAuth2RestTemplate template = new OAuth2RestTemplate(googleOpenId(), clientContext);

//        template.setInterceptors(Collections.singletonList(loggingInterceptor));


        return template;
    }

    @Bean
    @Description("Google API UserInfo resource server")
    public GoogleUserInfoTokenServices googleUserInfoTokenServices() {

        GoogleUserInfoTokenServices userInfoTokenServices =
                new GoogleUserInfoTokenServices(userInfoUri, clientId);

        return userInfoTokenServices;
    }



}