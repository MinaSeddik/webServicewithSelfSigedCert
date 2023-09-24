package com.example.springbootproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class DefaultWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("customAuthenticationEntryPoint")
    public AuthenticationEntryPoint authEntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.formLogin().disable();
        http.httpBasic().disable();

//        http.oauth2Client();

        /*

        Clickjacking attack prevention
        -------------------------------


        Reference: https://stackoverflow.com/questions/28647136/how-to-disable-x-frame-options-response-header-in-spring-security
        By default X-Frame-Options is set to denied, to prevent clickjacking attacks.

        DENY - is a default value. With this the page cannot be displayed in a frame, regardless of the site attempting to do so.
        SAMEORIGIN - I assume this is what you are looking for, so that the page will be (and can be) displayed in a frame on the same origin as the page itself
        ALLOW-FROM - Allows you to specify an origin, where the page can be displayed in a frame.

*/

        /*

         Spring default security headers
         https://docs.spring.io/spring-security/site/docs/5.0.x/reference/html/headers.html

         */
        http.authorizeRequests()
                .anyRequest()
                .permitAll();
//                .and()
//                .anonymous().disable();
//                .authenticated();
//                .headers()
//                .frameOptions().disable();  // disabled by default

        http.exceptionHandling()
                .authenticationEntryPoint(authEntryPoint);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    /*
    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {

        return new InMemoryOAuth2AuthorizedClientService(
                clientRegistrationRepository());
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
//        return new InMemoryClientRegistrationRepository(clientRegistration() );
        return null;
    }
*/

    /*
          By default, Spring OAuth2 uses HttpSessionOAuth2AuthorizationRequestRepository to save
          the authorization request. But, since our service is stateless, we can't save it in
          the session. We'll save the request in a Base64 encoded cookie instead.
        */

    /*
    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {

        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }
    */
}
