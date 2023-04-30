package com.example.springbootproject.config;

import com.example.springbootproject.security.CustomAuthenticationEntryPoint;
import com.example.springbootproject.security.GoogleUserInfoTokenServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Profile("oauth2-resttemplete")
@Configuration
@EnableWebSecurity
public class OAuth2ClientSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2RestTemplate restTemplate;

    @Autowired
    private GoogleUserInfoTokenServices googleUserInfoTokenServices;

    @Autowired
    private OAuth2ClientContextFilter oauth2ClientContextFilter;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/", "/static/**", "/webjars/**");
    }


    @Bean
    @Description("Filter that checks for authorization code, and if there's none, " +
            "acquires it from authorization server")
    public OAuth2ClientAuthenticationProcessingFilter oauth2ClientAuthenticationProcessingFilter() {

        OAuth2ClientAuthenticationProcessingFilter filter =
                new OAuth2ClientAuthenticationProcessingFilter("/google-login");
        filter.setRestTemplate(restTemplate);


        // Set a service that validates an OAuth2 access token
        // We can use either Google API's UserInfo or TokenInfo
        // For this, we chose to use UserInfo service
        filter.setTokenServices(googleUserInfoTokenServices);
        return filter;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new LoginUrlAuthenticationEntryPoint("/google-login");
    }

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.exceptionHandling()
//                .authenticationEntryPoint(authenticationEntryPoint())
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .and()
                .authorizeRequests()
//                .mvcMatchers("/signin-with-google.html")
//                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .logout((logout) ->
                        logout.deleteCookies("JSESSIONID")
                                .invalidateHttpSession(false)
                                .clearAuthentication(true)
                                .logoutUrl("/logout")  // should be implemented as Spring controller
                                .logoutSuccessUrl("/logout-success"))
                .addFilterAfter(
                        oauth2ClientContextFilter, ExceptionTranslationFilter.class)
                .addFilterBefore(
                        oauth2ClientAuthenticationProcessingFilter(), FilterSecurityInterceptor.class)
                .anonymous()
                // anonymous login must be disabled,
                // otherwise an anonymous authentication will be created,
                // and the UserRedirectRequiredException will not be thrown,
                // and the user will not be redirected to the authorization server
                .disable();
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
//        return new NoopAuthenticationManager();
        return authentication -> {
            throw new UnsupportedOperationException("No authentication should be done with this AuthenticationManager");
        };
    }

    private static class NoopAuthenticationManager implements AuthenticationManager {
        @Override
        public Authentication authenticate(Authentication authentication)
                throws AuthenticationException {
            throw new UnsupportedOperationException(
                    "No authentication should be done with this AuthenticationManager");
        }
    }

/*
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

//        http.anonymous().disable();

//        http
        // Handles unauthenticated requests, catching UserRedirectRequiredExceptions
                // and redirecting to OAuth provider
//            .addFilterAfter(new OAuth2ClientContextFilter(), SecurityContextPersistenceFilter.class)


                // Handles the oauth callback, exchanging the one-time code for a durable token
//                .addFilterAfter(openIdConnectFilter, OAuth2ClientContextFilter.class);

        http
                .addFilterAfter(new OAuth2ClientContextFilter(), AbstractPreAuthenticatedProcessingFilter.class)
//                .addFilterAfter(new OAuth2ClientContextFilter(), SecurityContextPersistenceFilter.class)
                .addFilterAfter(openIdConnectFilter(), OAuth2ClientContextFilter.class)
                .httpBasic()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/google-login"))
                .and()
                .authorizeRequests()
//                .mvcMatchers("/login.html", "/google-login", "/signin-with-google.html")
//                .permitAll()
                .anyRequest()
                .authenticated();


        return http.build();
    }
*/


}

