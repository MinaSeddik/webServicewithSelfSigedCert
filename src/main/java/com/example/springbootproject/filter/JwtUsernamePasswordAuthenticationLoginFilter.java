package com.example.springbootproject.filter;

import com.example.springbootproject.exception.InValidCredentialsException;
import com.example.springbootproject.domain.SecurityUserDetails;
import com.example.springbootproject.security.OtpAuthentication;
import com.example.springbootproject.security.UsernamePasswordAuthentication;
import com.example.springbootproject.util.JwtManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class JwtUsernamePasswordAuthenticationLoginFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtManager jwtManager;

    @Autowired
    private UserDetailsManager userDetailsManager;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("inside JwtUsernamePasswordAuthenticationLoginFilter ...");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String code = request.getParameter("code");

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new InValidCredentialsException(HttpStatus.UNAUTHORIZED, "Username or Password is missed!");
        }

        SecurityUserDetails userDetails = (SecurityUserDetails) userDetailsManager.loadUserByUsername(username);
        Authentication authentication = null;
        log.info("Code received: {}", code);
        if(userDetails.is2faEnabled() && code == null){

            Authentication authentication1 = new UsernamePasswordAuthentication(username, password);
            authentication = authenticationManager.authenticate(authentication1);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            try {
                String payload = objectMapper.writeValueAsString("please provide OTP code");
                log.info("Write payload {}", payload);
                response.getWriter()
                        .write(payload);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
        else if (userDetails.is2faEnabled() && code != null){
            Authentication authentication1 = new UsernamePasswordAuthentication(username, password);
            authentication = authenticationManager.authenticate(authentication1);


            Authentication authentication2 = new OtpAuthentication(username, code);
            authentication = authenticationManager.authenticate(authentication2);

        }else{
            Authentication authentication1 = new UsernamePasswordAuthentication(username, password);
            authentication = authenticationManager.authenticate(authentication1);
        }





        String roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Pair<String, String> jwtTokens = jwtManager.generateJwtTokens(authentication.getName(), roles);

        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("access_token", jwtTokens.getFirst());
        tokenResponse.put("refresh_token", jwtTokens.getSecond());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            String payload = objectMapper.writeValueAsString(tokenResponse);
            log.info("Write payload {}", payload);
            response.getWriter()
                    .write(payload);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/auth/login");
    }

}
