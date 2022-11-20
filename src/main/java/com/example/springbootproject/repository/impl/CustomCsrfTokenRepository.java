package com.example.springbootproject.repository.impl;

import com.example.springbootproject.model.Token;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

@Repository
@Slf4j
public class CustomCsrfTokenRepository implements CsrfTokenRepository {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MyAppCsrfTokenRepository myAppCsrfTokenRepository;

    /*
    curl -H "X-IDENTIFIER:12345" http://localhost:8080/repo

    curl -XPOST -H "X-IDENTIFIER:12345" -H "X-CSRF-TOKEN:2bc652f5-258b-4a26-b456-
            928e9bad71f8" http://localhost:8080/repo
    */

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        String uuid = UUID.randomUUID().toString();

        log.info("generateToken::Csrf Token: {}", uuid);

        return new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", uuid);
    }

    @Override
    public void saveToken(CsrfToken csrfToken, HttpServletRequest request, HttpServletResponse response) {
        String identifier = request.getHeader("X-IDENTIFIER");
        log.info("saveToken::X-IDENTIFIER Value: {}", identifier);

        if(Strings.isBlank(identifier)){
            return;
        }

        Optional<Token> existingToken = myAppCsrfTokenRepository.findTokenByIdentifier(identifier);

        if (existingToken.isPresent()) {
            log.info("saveToken::Token for identifier {} exists, CSRF token: {}", identifier, existingToken.get().getToken());

            Token token = existingToken.get();
            token.setToken(csrfToken.getToken());
        } else {
            log.info("saveToken::Token for identifier {} does NOT exist, CSRF token: {}", identifier, csrfToken.getToken());

            Token token = Token.builder()
                    .identifier(identifier)
                    .token(csrfToken.getToken())
                    .build();

            log.info("saveToken::Save token: {} for identifier {}", csrfToken.getToken(), identifier);
            int saved = myAppCsrfTokenRepository.save(token);
            log.info("saveToken::Token: {} [{}]", csrfToken.getToken(), saved>0 ? "Saved": "NOT Saved");
        }
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {

        String identifier = request.getHeader("X-IDENTIFIER");
        log.info("loadToken::X-IDENTIFIER Value: {}", identifier);

        if(Strings.isBlank(identifier)){
            return null;
        }
        Optional<Token> existingToken = myAppCsrfTokenRepository.findTokenByIdentifier(identifier);

        if (existingToken.isPresent()) {
            log.info("loadToken::Token exists for identifier {}: {}", identifier, existingToken.get().getToken());
            Token token = existingToken.get();
            return new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", token.getToken());
        }

        log.info("loadToken::Token does NOT exist for identifier {}", identifier);
        return null;
    }
}
