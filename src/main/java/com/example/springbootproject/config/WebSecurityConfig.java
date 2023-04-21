package com.example.springbootproject.config;

import com.example.springbootproject.filter.CsrfTokenLogger;
import com.example.springbootproject.filter.RequestValidationFilter;
import com.example.springbootproject.repository.impl.CustomCsrfTokenRepository;
import com.example.springbootproject.security.CustomAuthenticationFailureHandler;
import com.example.springbootproject.security.CustomAuthenticationSuccessHandler;
import com.example.springbootproject.service.MyAppAuthenticationProvider;
import com.example.springbootproject.service.UsernamePasswordAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Profile("general-security")
@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    private AuthenticationProvider authenticationProvider;

    @Autowired
    private CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private CustomAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    @Qualifier("customAuthenticationEntryPoint")
    public AuthenticationEntryPoint authEntryPoint;

    @Autowired
    public AccessDeniedHandler accessDeniedHandler;

    @Autowired
    public MyAppAuthenticationProvider myAppAuthenticationProviderService;

    @Autowired
    public UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.formLogin().disable();
        http.httpBasic().disable();

    }

    public void configure_oauth2_client_config(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.formLogin().disable();
        http.httpBasic().disable();

//        http.oauth2Login();

//        http.oauth2Login(c -> {
//            c.clientRegistrationRepository(clientRepository());
//        });

        http.authorizeRequests()
                .anyRequest()
                .authenticated();
    }

    /*
    Adds the AuthenticationManager to the Spring context so that we can auto-wire it
    from the filter class
 */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(usernamePasswordAuthenticationProvider);
    }


    public void configure_rest_api_login(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.formLogin().disable();
        http.httpBasic().disable();

        http.csrf()
                .disable()
                .authorizeRequests()
                .mvcMatchers("/login", "/login.html", "/signup", "/signup.html")
                .permitAll()

                // to test accessDeniedHandler
                .mvcMatchers("/repo")
                .hasAuthority("DELETE")

                .anyRequest().authenticated();

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


    }

    public HttpSessionRequestCache getHttpSessionRequestCache() {
        HttpSessionRequestCache httpSessionRequestCache = new HttpSessionRequestCache();
        httpSessionRequestCache.setCreateSessionAllowed(false); // I modified this parameter
        return httpSessionRequestCache;
    }

    public void configure_CORS(HttpSecurity http) throws Exception {

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

        http.csrf().disable();
        http.httpBasic();

        http.authorizeRequests()
                .anyRequest()
//                .authenticated();
                .permitAll();

    }

    public void configure_CSRF(HttpSecurity http) throws Exception {

//        http.csrf().disable();

        http.csrf(c -> {
            // curl -H "X-IDENTIFIER:12345" http://localhost:8080/repo
            /*
                curl -H "X-IDENTIFIER:12345" http://localhost:8080/repo

                curl -XPOST -H "X-IDENTIFIER:12345" -H "X-CSRF-TOKEN:2bc652f5-258b-4a26-b456-928e9bad71f8" http://localhost:8080/repo
            */

            c.csrfTokenRepository(customTokenRepository());

            // exclude the path from the CSRF protection mechanism.
//            c.ignoringAntMatchers("/ciao");
        });

        // the same as
//        HandlerMappingIntrospector i = new HandlerMappingIntrospector();
//        MvcRequestMatcher mvcRequestMatcher = new MvcRequestMatcher(i, "/ciao");
//        http.csrf().ignoringRequestMatchers(mvcRequestMatcher);

        // yet another example
//        String pattern = ".*[0-9].*";
//        String httpMethod = HttpMethod.POST.name();
//        RegexRequestMatcher regexRequestMatcher = new RegexRequestMatcher(pattern, httpMethod);
//        http.csrf().ignoringRequestMatchers(regexRequestMatcher);


        http.addFilterAfter(new CsrfTokenLogger(), CsrfFilter.class)
                .authorizeRequests()
                .anyRequest()
//                .authenticated();
                .permitAll();

        http.formLogin()
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler);


    }


    @Bean
    public CsrfTokenRepository customTokenRepository() {
        return new CustomCsrfTokenRepository();
    }

    public void configure_Filters(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.httpBasic();


        http.addFilterBefore(new RequestValidationFilter(), BasicAuthenticationFilter.class)
                .authorizeRequests()
                .anyRequest()
//                .authenticated();
                .permitAll();


    }

    //    @Override
    public void configure_Authorization_Matchers(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.httpBasic();

        http.authorizeRequests()
                .anyRequest()
//                .permitAll();
//                .denyAll()
//                .authenticated();

//                .hasAuthority("DELETE");
                .hasAnyAuthority("READ", "WRITE");
//                .access("hasAuthority('read') and !hasAuthority('delete')");

//                 the endpoint to be allowed only after 12:00 pm.
//                .access("T(java.time.LocalTime).now().isAfter(T(java.time.LocalTime).of(12, 0))");

//                .hasRole("ADMIN");   // Mind that that ROLE_ prefix does not appear here.
//                .hasAnyRole("ADMIN", "MANAGER", "REGULAR");   // Mind that that ROLE_ prefix does not appear here.
//                .access("hasRole('ADMIN') and !hasAnyRole('REGULAR')");


//        the order of the rules should be from particular to general.
        http.authorizeRequests()
                .mvcMatchers("/hello").hasRole("ADMIN")
                .mvcMatchers("/ciao").hasRole("MANAGER")
                .anyRequest().permitAll();

        // yet another example of HTTP methods
        http.authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/a")
                .authenticated()
                .mvcMatchers(HttpMethod.POST, "/a")
                .permitAll()
                .anyRequest()
                .denyAll();

        // more example for path with pattern
        // the ** operator refers to any number of pathnames.
        http.authorizeRequests()
                .mvcMatchers("/a/b/**")
//                .mvcMatchers( "/a/**/c")
//                .mvcMatchers( "/a/*/c")   // match one path name
//                .mvcMatchers("/product/{code:^[0-9]*$}")  // The regex refers to strings of any length, containing any digit.
//                .regexMatchers(".*/(us|uk|ca)+/(en|fr).*")
                .authenticated()
                .anyRequest()
                .permitAll();

    }

    public void configure_AuthenticationType(HttpSecurity http) throws Exception {
//        http.authorizeRequests().anyRequest().permitAll();
//        http.csrf().disable();

        http.httpBasic();

//        http.httpBasic()
//                .and()
//                .formLogin();

//        http.formLogin()
//                .successHandler(authenticationSuccessHandler)
//                .failureHandler(authenticationFailureHandler)
//                .and()
//                .httpBasic();

        http.authorizeRequests()
                .anyRequest().authenticated();
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