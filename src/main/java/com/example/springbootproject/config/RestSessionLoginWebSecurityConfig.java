package com.example.springbootproject.config;


import com.example.springbootproject.service.MyAppAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.List;

@Profile("rest-login")
@Configuration
@EnableWebSecurity

//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
/*
The prePostEnabled property enables Spring Security pre/post annotations.
The securedEnabled property determines if the @Secured annotation should be enabled.
The jsr250Enabled property allows us to use the @RoleAllowed annotation.
 */
public class RestSessionLoginWebSecurityConfig extends WebSecurityConfigurerAdapter implements WebApplicationInitializer {

    @Autowired
    @Qualifier("customAuthenticationEntryPoint")
    public AuthenticationEntryPoint authEntryPoint;

    @Autowired
    public AccessDeniedHandler accessDeniedHandler;

    @Autowired
    public MyAppAuthenticationProvider myAppAuthenticationProvider;


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(myAppAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.formLogin().disable();
        http.httpBasic().disable();

        /*

        // Matchers Examples
        http.authorizeRequests().antMatchers(HttpMethod.GET).permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST).denyAll();
        http.authorizeRequests().antMatchers(HttpMethod.DELETE,"/you/can/alsoSpecifyAPath").denyAll();
        http.authorizeRequests().antMatchers(HttpMethod.PATCH,"/path/is/Case/Insensitive").denyAll();
        http.authorizeRequests().antMatchers(HttpMethod.PUT,"/and/can/haveWildcards/*").denyAll();

        */

        // Regarding CORS
        // https://stackoverflow.com/questions/11423682/cross-domain-form-posting
//        http.cors().disable();
        http.cors(c -> {
            CorsConfigurationSource source = request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("example.com", "example.org"));
//                config.setAllowedOriginPatterns(List.of("example.com", "example.org"));

                /* you have to specify at least which are the origins
                and the methods. If you only specify the origins, your application won’t allow the
                requests. This behavior happens because a CorsConfiguration object doesn’t
                define any methods by default. */
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                return config;
            };
            c.configurationSource(source);
        });

        // Regarding CSRF
        // Starting from Spring Security 4.x, the CSRF protection is enabled by default.
//        http.csrf().disable();
//        https://www.baeldung.com/spring-security-csrf
//        Our stateless API can't add the CSRF token like our MVC configuration because it doesn't generate any HTML view.
//        This configuration will set a XSRF-TOKEN cookie to the front end. Because we set the HTTP-only flag to false, the front end will be able to retrieve this cookie using JavaScript.
        http.csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

/*
        cookies Hints:

        |-------------------------------------------------------------------------------------------------------|
        | Vulnerability	| Brief	                                | Mitigation                                    |
        |-------------------------------------------------------------------------------------------------------|
        | XSS	        | The client side js can read cookies	| HttpOnly cookie                               |
        | CSRF	        | Cookies are sent to the attacker	    | CORS polciy, X-CSRF-TOKEN, SameSite cookie    |
        |-------------------------------------------------------------------------------------------------------|
*/
        http.csrf()
                .disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
                .authorizeRequests()
                .mvcMatchers("/login", "/login.html", "/signup", "/signup.html")
                .permitAll()
                .anyRequest().authenticated();

        /*
        always – A session will always be created if one doesn't already exist.
        ifRequired – A session will be created only if required (default).
        never – The framework will never create a session itself, but it will use one if it already exists.
        stateless – No session will be created or used by Spring Security.
        */
//        http.sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

        http.sessionManagement()
//                .expiredUrl("/sessionExpired.html")
                .invalidSessionUrl("/invalidSession.html");

// Session timeout - in application.properties
        // 15m = 15 minutes
//        server.servlet.session.timeout=15m

        http.requestCache()
                .requestCache(getHttpSessionRequestCache());

        http.logout()
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)));


        http.exceptionHandling()
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);


        // more security concerns
//        1) X-XSS-Protection in apache or Nginx to avoid clickjacking attack
//        2) Open redirection preventing, Don't let the user control the redirection
//                check that: https://www.stackhawk.com/blog/angular-open-redirect-guide-examples-and-prevention/
//        3) File traversal injection
//                check DesignPatterPalying project, pathnorm package Main class
//        3) OS command injection


    }

    @Override
    public void onStartup(ServletContext sc) throws ServletException {

        sc.getSessionCookieConfig().setHttpOnly(true);
        sc.getSessionCookieConfig().setSecure(true);
    }

    public HttpSessionRequestCache getHttpSessionRequestCache() {
        HttpSessionRequestCache httpSessionRequestCache = new HttpSessionRequestCache();
        httpSessionRequestCache.setCreateSessionAllowed(false); // I modified this parameter
        return httpSessionRequestCache;
    }

}
