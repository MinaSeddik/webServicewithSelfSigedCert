package com.example.springbootproject.filter;

import com.example.springbootproject.dto.RestResponse;
import com.example.springbootproject.exception.InValidJwtTokenException;
import com.example.springbootproject.exception.MissedAuthorizationHeaderException;
import com.example.springbootproject.repository.JwtBlacklistRepository;
import com.example.springbootproject.repository.impl.JwtDatabaseBlacklistRepository;
import com.example.springbootproject.util.JwtManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;

@Slf4j
public class JwtUsernamePasswordAuthenticationLogoutFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtBlacklistRepository jwtBlacklistRepository;

    @Autowired
    private JwtManager jwtManager;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("inside JwtUsernamePasswordAuthenticationLogoutFilter ...");

        String jwtAccessToken = request.getHeader("Authorization");

        if (StringUtils.isEmpty(jwtAccessToken)) {
            throw new MissedAuthorizationHeaderException("JWT access token is missed!");
        }


        Claims claims;
        try {
            claims = jwtManager.getClaims(jwtAccessToken);
        } catch (ExpiredJwtException ex) {
            throw new InValidJwtTokenException("Expired JWT access token!");
        } catch (JwtException ex) {
            throw new InValidJwtTokenException("Invalid JWT token!");
        }

        String username = claims.getSubject();
        String token = claims.getId();

        // blacklist the token ...
        jwtBlacklistRepository.blacklist(username, token);

        RestResponse restResponse = RestResponse.builder().username(username)
                .timestamp(Instant.now().toString())
                .status(HttpStatus.OK.toString())
                .message("user logged out!")
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);

        try {
            OutputStream responseStream = response.getOutputStream();

            objectMapper.writeValue(responseStream, restResponse);
            responseStream.flush();
        } catch (Exception ex) {
            log.error("Error: ", ex);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/auth/logout");
    }

}

