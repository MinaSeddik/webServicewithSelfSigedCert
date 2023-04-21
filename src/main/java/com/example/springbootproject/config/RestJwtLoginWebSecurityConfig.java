package com.example.springbootproject.config;

import com.example.springbootproject.filter.*;
import com.example.springbootproject.service.OtpAuthenticationProvider;
import com.example.springbootproject.service.UsernamePasswordAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Profile("jwt-login")
@Configuration
@EnableWebSecurity
public class RestJwtLoginWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("customAuthenticationEntryPoint")
    public AuthenticationEntryPoint authEntryPoint;

    @Autowired
    public AccessDeniedHandler accessDeniedHandler;

    @Autowired
    public UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;

    @Autowired
    public OtpAuthenticationProvider otpAuthenticationProvider;

    @Autowired
    public ExceptionHandlerFilter exceptionHandlerFilter;


    @Bean
    public JwtUsernamePasswordAuthenticationLoginFilter jwtUsernamePasswordAuthenticationFilter() {
        return new JwtUsernamePasswordAuthenticationLoginFilter();
    }

    @Bean
    public JwtUsernamePasswordAuthenticationLogoutFilter jwtUsernamePasswordAuthenticationLogoutFilter() {
        return new JwtUsernamePasswordAuthenticationLogoutFilter();
    }

    @Bean
    public JwtUsernamePasswordAuthenticationRefreshFilter jwtUsernamePasswordAuthenticationRefreshFilter() {
        return new JwtUsernamePasswordAuthenticationRefreshFilter();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(usernamePasswordAuthenticationProvider)
                .authenticationProvider(otpAuthenticationProvider);

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.formLogin().disable();
        http.httpBasic().disable();

        http.csrf()
                .disable()
                .authorizeRequests()
                .mvcMatchers("/auth/login", "/auth/refresh", "/signup.html", "/signup", "/qr.html")
                .permitAll()
                .anyRequest().authenticated();

        /*
        always – A session will always be created if one doesn't already exist.
        ifRequired – A session will be created only if required (default).
        never – The framework will never create a session itself, but it will use one if it already exists.
        stateless – No session will be created or used by Spring Security.
        */
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);


        http.addFilterBefore(exceptionHandlerFilter, LogoutFilter.class)
                .addFilterAt(jwtUsernamePasswordAuthenticationFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(jwtUsernamePasswordAuthenticationRefreshFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(jwtUsernamePasswordAuthenticationLogoutFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter(), BasicAuthenticationFilter.class);

        http.exceptionHandling()
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

    }

}