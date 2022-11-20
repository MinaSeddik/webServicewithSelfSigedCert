package com.example.springbootproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InValidJwtTokenException extends ResponseStatusException {

    public InValidJwtTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

    public InValidJwtTokenException(HttpStatus status, String message) {
        super(status, message);
    }
}
