package com.example.springbootproject.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InValidCredentialsException extends ResponseStatusException {

    public InValidCredentialsException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

    public InValidCredentialsException(HttpStatus status, String message) {
        super(status, message);
    }
}


