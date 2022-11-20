package com.example.springbootproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MissedAuthorizationHeaderException extends ResponseStatusException {

    public MissedAuthorizationHeaderException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

    public MissedAuthorizationHeaderException(HttpStatus status, String message) {
        super(status, message);
    }
}
