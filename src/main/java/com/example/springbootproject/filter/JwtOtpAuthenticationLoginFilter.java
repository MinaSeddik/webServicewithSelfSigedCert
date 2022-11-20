package com.example.springbootproject.filter;

import com.example.springbootproject.exception.InValidCredentialsException;
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
public class JwtOtpAuthenticationLoginFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtManager jwtManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("inside JwtOtpAuthenticationLoginFilter ...");

        String username = request.getParameter("username");
        String code = request.getParameter("otp");

        if (StringUtils.isEmpty(username)) {
            throw new InValidCredentialsException(HttpStatus.UNAUTHORIZED, "Username is missed!");
        }

        if (StringUtils.isEmpty(code)) {
            throw new InValidCredentialsException(HttpStatus.UNAUTHORIZED, "OTP code is missed!");
        }

        Authentication authentication = new OtpAuthentication(username, code);
        authentication = authenticationManager.authenticate(authentication);



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
        return !request.getServletPath().equals("/auth/otp");
    }

}
