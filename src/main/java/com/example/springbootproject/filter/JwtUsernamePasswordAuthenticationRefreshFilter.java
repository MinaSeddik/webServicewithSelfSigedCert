package com.example.springbootproject.filter;

import com.example.springbootproject.exception.InValidJwtRefreshTokenException;
import com.example.springbootproject.exception.InValidJwtTokenException;
import com.example.springbootproject.exception.LockedAccountException;
import com.example.springbootproject.exception.MissedAuthorizationHeaderException;
import com.example.springbootproject.repository.JwtBlacklistRepository;
import com.example.springbootproject.service.MyAppUserDetailsService;
import com.example.springbootproject.util.JwtManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class JwtUsernamePasswordAuthenticationRefreshFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyAppUserDetailsService myAppUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtManager jwtManager;

    @Autowired
    private JwtBlacklistRepository jwtBlacklistRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("inside JwtUsernamePasswordAuthenticationLogoutFilter ...");

        String jwtRefreshToken = request.getHeader("Authorization");

        if (StringUtils.isEmpty(jwtRefreshToken)) {
            throw new MissedAuthorizationHeaderException("JWT refresh token is missed!");
        }


        Claims claims;
        try {
            claims = jwtManager.getClaims(jwtRefreshToken);
        } catch (ExpiredJwtException ex) {
            throw new InValidJwtTokenException("Expired JWT refresh token!");
        } catch (JwtException ex) {
            throw new InValidJwtTokenException("Invalid JWT token!");
        }


        String tokenType = claims.get("tokenType", String.class);
        if (!tokenType.equals("RefreshToken")) {
            throw new InValidJwtRefreshTokenException("JWT refresh token is missed!");
        }

        String user = claims.getSubject();
        String token = claims.getId();

        // is it blacklisted ?!
        boolean isInValidToken = jwtBlacklistRepository.isTokenBlacklisted(user, token);
        if (isInValidToken) {
            throw new InValidJwtTokenException("Invalid JWT token!");
        }

        // if the user is deleted (will throw exception) or become blocked
        UserDetails userDetails = myAppUserDetailsService.loadUserByUsername(user);
        if (!userDetails.isAccountNonLocked()) {
            throw new LockedAccountException("User account is locked!");
        }

        // in case, roles changed
        String roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));


        String accessJwt = jwtManager.generateAccessToken(userDetails.getUsername(), token, roles);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            response.getWriter()
                    .write(objectMapper.writeValueAsString(Map.of("access_token", accessJwt)));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/auth/refresh");
    }

}
