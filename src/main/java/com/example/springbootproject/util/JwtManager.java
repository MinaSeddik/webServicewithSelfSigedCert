package com.example.springbootproject.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtManager {

    private final static String TOKEN_TYPE = "JWT";

    @Value("${app.name}")
    private String issuerAppName;

    @Value("${jwt.accessToken.timeout}")
    private Duration accessTokenDuration;

    @Value("${jwt.refreshToken.timeout}")
    private Duration refreshTokenDuration;

    @Value("${jwt.signing.key}")
    private String signingKey;


    public Pair<String, String> generateJwtTokens(String username, String roles) {

        String token = UUID.randomUUID().toString();

        String refreshJwt = generateRefreshToken(username, token);
        String accessJwt = generateAccessToken(username, token, roles);

        return Pair.of(accessJwt, refreshJwt);

    }

    private String generateRefreshToken(String username, String token) {
        // issued At
        Instant iat = Instant.now();

        // expire At
        Instant exp = Instant.now().plus(refreshTokenDuration);

        SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setHeaderParam("typ", TOKEN_TYPE)
                .setClaims(Map.of("tokenType", "RefreshToken"))
                .setIssuer(issuerAppName)
                .setSubject(username)
                .setIssuedAt(Date.from(iat)) // for example, now
                .setExpiration(Date.from(exp)) //a java.util.Date
                .setId(token) //just an example id - jti - jwt id
                .signWith(key)
                .compact();
    }

    public String generateAccessToken(String username, String token, String roles) {
        // issued At
        Instant iat = Instant.now();

        // expire At
        Instant exp = Instant.now().plus(accessTokenDuration);

        SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setHeaderParam("typ", TOKEN_TYPE)
                .setClaims(Map.of("scope", roles, "tokenType", "AccessToken"))
                .setIssuer(issuerAppName)
                .setSubject(username)
//                .setAudience("MyApp-Clients")  // could be omitted
                .setIssuedAt(Date.from(iat)) // for example, now
                .setExpiration(Date.from(exp)) //a java.util.Date
                .setId(token) //just an example id - jti - jwt id
                .signWith(key)
                .compact();
    }

    public Claims getClaims(String jwtToken) throws ExpiredJwtException, JwtException {

        SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

}
