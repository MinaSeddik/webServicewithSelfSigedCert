package com.example.springbootproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.UUID;

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll();
        http.csrf().disable();
    }


    /*
    remove this bean

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(this.adminServer.getContextPath() + "/");

        http.authorizeRequests()
                .antMatchers(this.adminServer.getContextPath() + "/assets/**").permitAll()
                .antMatchers(this.adminServer.getContextPath() + "/login").permitAll()
//                .anyRequest().authenticated()
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage(this.adminServer.getContextPath() + "/login")
                .successHandler(successHandler)
                .and()
                .logout()
                .logoutUrl(this.adminServer.getContextPath() + "/logout")
                .and()
                .httpBasic()
                .and()
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers(
                        new AntPathRequestMatcher(this.adminServer.getContextPath() +
                                "/instances", HttpMethod.POST.toString()),
                        new AntPathRequestMatcher(this.adminServer.getContextPath() +
                                "/instances/*", HttpMethod.DELETE.toString()),
                        new AntPathRequestMatcher(this.adminServer.getContextPath() + "/actuator/**"))
                .and()
                .rememberMe()
                .key(UUID.randomUUID().toString())
                .tokenValiditySeconds(1209600);


        return http.build();
    }

*/


    /*

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange()
                .pathMatchers("/actuator/").hasRole("ACTUATOR")
                .anyExchange().authenticated()
                .and().build();
    }

     */
}