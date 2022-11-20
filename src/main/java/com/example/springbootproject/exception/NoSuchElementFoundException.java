package com.example.springbootproject.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoSuchElementFoundException extends ResponseStatusException {

    public NoSuchElementFoundException(String message){
        super(HttpStatus.NOT_FOUND, message);
    }

    @Override
    public HttpHeaders getResponseHeaders() {
        // return response headers

        return null;
    }
}