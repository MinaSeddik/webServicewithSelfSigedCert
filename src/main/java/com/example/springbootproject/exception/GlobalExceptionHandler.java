package com.example.springbootproject.exception;

import com.example.springbootproject.dto.RestErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

//@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Autowired
    public ObjectMapper mapper;

    @ExceptionHandler(MyException.class)
    public void handleMyException(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Exception exception) {

        String trackId = UUID.randomUUID().toString();
        log.error("{} - {}", trackId, exception.getMessage(), exception);

        RestErrorResponse restError = RestErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .traceId(trackId)
                .status(HttpStatus.BAD_REQUEST.toString())
//                .status(HttpStatus.CONFLICT.toString())
                .error(exception.getMessage())
                .path(request.getServletPath())
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        try {
            OutputStream responseStream = response.getOutputStream();

            mapper.writeValue(responseStream, restError);
            responseStream.flush();
        } catch (Exception ex) {
            log.error("Error: ", ex);
        }

    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Object> handleMyException2(HttpServletRequest request,
                                                     Exception exception) {

        String trackId = UUID.randomUUID().toString();
        log.error("{} - {}", trackId, exception.getMessage(), exception);

        RestErrorResponse restErrorResponse = RestErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .traceId(trackId)
                .status(HttpStatus.BAD_REQUEST.toString())
//                .status(HttpStatus.CONFLICT.toString())
                .error(exception.getMessage())
                .path(request.getServletPath())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(restErrorResponse);

    }


    @ExceptionHandler(SQLException.class)
    public String handleSQLException(HttpServletRequest request, Exception ex) {
        log.info("SQLException occurred:: URL=" + request.getRequestURL());
        return "database_error";
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "IOException occurred")
    @ExceptionHandler(IOException.class)
    public void handleIOException() {
        log.error("IOException handler executed");
        //returning 404 error code
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Validation error occurred")
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleMethodArgumentNotValidException() {
        log.error("MethodArgumentNotValidException handler executed ************************");
        //returning 404 error code
    }


    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {

        String bodyOfResponse = "This should be application specific";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    private ResponseEntity<Object> handleExceptionInternal(RuntimeException ex,
                                                           String bodyOfResponse,
                                                           HttpHeaders httpHeaders,
                                                           HttpStatus conflict,
                                                           WebRequest request) {
        // ...
        return ResponseEntity.badRequest()
                .body(bodyOfResponse);
    }


}