package com.example.springbootproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class LockedAccountException extends ResponseStatusException {

    public LockedAccountException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }

    public LockedAccountException(HttpStatus status, String message) {
        super(status, message);
    }
}

