package com.example.springbootproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InValidJwtRefreshTokenException extends ResponseStatusException {

    public InValidJwtRefreshTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

    public InValidJwtRefreshTokenException(HttpStatus status, String message) {
        super(status, message);
    }
}

