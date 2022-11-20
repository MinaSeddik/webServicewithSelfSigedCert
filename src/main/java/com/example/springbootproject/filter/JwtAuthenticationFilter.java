package com.example.springbootproject.filter;

import com.example.springbootproject.exception.InValidJwtTokenException;
import com.example.springbootproject.repository.JwtBlacklistRepository;
import com.example.springbootproject.security.UsernamePasswordAuthentication;
import com.example.springbootproject.util.JwtManager;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtBlacklistRepository jwtBlacklistRepository;

    @Autowired
    private JwtManager jwtManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

//        String jwtToken = request.getHeader("Authorization");
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.isEmpty(jwtToken)) {
            throw new AccessDeniedException("Missed Authorization Header!");
        }

        Claims claims;
        try {
            claims = jwtManager.getClaims(jwtToken);
        } catch (ExpiredJwtException ex) {
            throw new InValidJwtTokenException("Expired JWT access token!");
        } catch (JwtException ex) {
            throw new InValidJwtTokenException("Invalid JWT token!");
        }

        String username = claims.getSubject();
        String token = claims.getId();

        // is it blacklisted ?!
        boolean isInValidToken = jwtBlacklistRepository.isTokenBlacklisted(username, token);
        if (isInValidToken) {
            throw new InValidJwtTokenException("Invalid JWT token!");
        }

        // check the token type is Access Token (NOT Refresh Token)
        String tokenType = claims.get("tokenType", String.class);
        if (!tokenType.equals("AccessToken")) {
            throw new InValidJwtTokenException("Invalid JWT access token!");
        }

        String roles = claims.get("scope", String.class);

        List<SimpleGrantedAuthority> authorities = Stream.of(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());


        var auth = new UsernamePasswordAuthentication(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        return request.getServletPath().equals("/auth/login") ||
                request.getServletPath().equals("/auth/refresh") ||
                request.getServletPath().equals("/auth/logout") ||
                request.getServletPath().equals("/signup.html") ||
                request.getServletPath().equals("/signup") ||
                request.getServletPath().equals("/qr.html") ||
                request.getServletPath().equals("/favicon.ico");
    }


}
