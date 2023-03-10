package com.example.springbootproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

//@Profile("oauth2-login")
@Configuration
public class OAuth2ClientRegistrationSecurityConfig {


    private ClientRegistration clientRegistration() {


        // spring set up the redirect URI:  /login/oauth2/code/{registrationId}
        // for Google: http://localhost:8080/login/oauth2/code/google


        return CommonOAuth2Provider.GOOGLE
                .getBuilder("google")
                .clientId("1052945576635-vv55b7jprva8ss2vs02lgdj0d4dh70u6.apps.googleusercontent.com")
                .clientSecret("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")
                .build();
    }


//    @Bean
//    public ClientRegistrationRepository clientRegistrationRepository() {
//        return new InMemoryClientRegistrationRepository(clientRegistration() );
//    }


    /*
    private List<ClientRegistration> clientRegistrations() {

        ClientRegistration githubClientRegistration = CommonOAuth2Provider.GITHUB
                .getBuilder("github")
                .clientId("a7553955a0c534ec5e6b")
                .clientSecret("1795b30b425ebb79e424afa51913f1c724da0dbb")
                .build();

        ClientRegistration googleClientRegistration = CommonOAuth2Provider.GOOGLE
                .getBuilder("google")
                .clientId("1052945576635-vv55b7jprva8ss2vs02lgdj0d4dh70u6.apps.googleusercontent.com")
                .clientSecret("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")
                .build();

        ClientRegistration facebookClientRegistration = CommonOAuth2Provider.FACEBOOK
                .getBuilder("facebook")
                .clientId("a7553955a0c534ec5e6b")
                .clientSecret("1795b30b425ebb79e424afa51913f1c724da0dbb")
                .build();

//        ClientRegistration oktaClientRegistration = CommonOAuth2Provider.OKTA
//                .getBuilder("okta")
//                .clientId("a7553955a0c534ec5e6b")
//                .clientSecret("1795b30b425ebb79e424afa51913f1c724da0dbb")
//                .build();

        // Custom configuration
//        ClientRegistration clientRegistration =
//                ClientRegistration.withRegistrationId("github")
//                        .clientId("a7553955a0c534ec5e6b")
//                        .clientSecret("1795b30b425ebb79e424afa51913f1c724da0dbb")
//                        .scope(new String[]{"read:user"})
//                        .authorizationUri("https://github.com/login/oauth/authorize")
//                        .tokenUri("https://github.com/login/oauth/access_token")
//                        .userInfoUri("https://api.github.com/user")
//                        .userNameAttributeName("id")
//                        .clientName("GitHub")
//                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
////                        .redirectUriTemplate("{baseUrl}/{action}/oauth2/code/{registrationId}")
//                        .redirectUri("{baseUrl}/{action}/oauth2/code/{registrationId}")
//                        .build();

        return List.of(githubClientRegistration,
                googleClientRegistration,
                facebookClientRegistration);
    }

    private ClientRegistration clientRegistration() {

        return CommonOAuth2Provider.FACEBOOK
                .getBuilder("facebook")
                .clientId("a7553955a0c534ec5e6b")
                .clientSecret("1795b30b425ebb79e424afa51913f1c724da0dbb")
                .build();
    }

    private ClientRegistration clientRegistration2() {

        return  CommonOAuth2Provider.GITHUB
                .getBuilder("github")
                .clientId("a7553955a0c534ec5e6b")
                .clientSecret("1795b30b425ebb79e424afa51913f1c724da0dbb")
                .build();
    }


    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {

//        ClientRegistration googleRegistration = clientRegistration();
//        ClientRegistration githubRegistration = clientRegistration2();
//        return new InMemoryClientRegistrationRepository(githubRegistration);
//        return new InMemoryClientRegistrationRepository(Arrays.asList(googleRegistration,
//                githubRegistration));


        List<ClientRegistration> registrations = clientRegistrations();
        return new InMemoryClientRegistrationRepository(registrations);
    }

*/





}
