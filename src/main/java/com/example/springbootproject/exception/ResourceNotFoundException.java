package com.example.springbootproject.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResourceNotFoundException extends ResponseStatusException {

    public ResourceNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Resource not found");
    }

    @Override
    public HttpHeaders getResponseHeaders() {
        // return response headers

        return null;
    }

}
