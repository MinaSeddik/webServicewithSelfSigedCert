package com.example.springbootproject.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Profile("oauth2-login")
@Configuration
@EnableWebFluxSecurity
@EnableWebSecurity
@Slf4j
public class OAuth2ReactiveClientRegistrationSecurityConfig extends WebSecurityConfigurerAdapter {

    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests()
//                .mvcMatchers("signin-with-google.html").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .loginPage("/signin-with-google.html");
//                .and()
//                .logout((logout) ->
//                        logout.deleteCookies("JSESSIONID")
//                                .invalidateHttpSession(false)
//                                .clearAuthentication(true)
//                                .logoutUrl("/logout")  // should be implemented as Spring controller
//                                .logoutSuccessUrl("/logout-success"));
//                .anonymous().disable()
//                .csrf().disable()
//                .headers()
//                .frameOptions()
//                .disable();


//        http.logout().logoutSuccessUrl("/index")        //permitall
//                .and()
//                .csrf().disable()
//                .headers().frameOptions().disable();

//                .clientRegistrationRepository(clientRegistrationRepository())
//                .authorizedClientService(authorizedClientService());


        return http.build();
    }

    @Bean
    public WebClient webClient(ReactiveClientRegistrationRepository clientRegistrations) {

        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                        clientRegistrations,
                        new UnAuthenticatedServerOAuth2AuthorizedClientRepository());

        // should be changed accordingly to the requirements
        oauth.setDefaultClientRegistrationId("google");
//        oauth.setDefaultOAuth2AuthorizedClient(true);


        return WebClient.builder()
                .filter(oauth)
                .build();
    }

    @Bean
    public ReactiveOAuth2AuthorizedClientService authorizedClientService() {

        // to be changed later - not in-memory
        return new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository());
    }


//    @Bean
//    public OAuth2AuthorizedClientService authorizedClientService() {
//
//        // to be changed later - not in-memory
//        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
//    }

    @Bean
    public ReactiveClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = clientRegistrations();

        // to be changed later - not in-memory
        return new InMemoryReactiveClientRegistrationRepository(registrations);
    }


//    @Bean
//    public ClientRegistrationRepository clientRegistrationRepository() {
//        List<ClientRegistration> registrations = clientRegistrations();
//
//        // to be changed later - not in-memory
//        return new InMemoryClientRegistrationRepository(registrations);
//    }


    private List<ClientRegistration> clientRegistrations() {

        // spring set up the redirect URI:  /login/oauth2/code/{registrationId}
        // for Google: http://localhost:8080/login/oauth2/code/google
        ClientRegistration googleClientRegistration = CommonOAuth2Provider.GOOGLE
                .getBuilder("google")
                .clientId("1052945576635-vv55b7jprva8ss2vs02lgdj0d4dh70u6.apps.googleusercontent.com")
                .clientSecret("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")
//                .scope("email, profile")
                .build();


        ClientRegistration githubClientRegistration = CommonOAuth2Provider.GITHUB
                .getBuilder("github")
                .clientId("a7553955a0c534ec5e6b")
                .clientSecret("1795b30b425ebb79e424afa51913f1c724da0dbb")
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

}
