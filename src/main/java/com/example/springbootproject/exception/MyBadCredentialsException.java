package com.example.springbootproject.exception;

public class MyBadCredentialsException extends Exception{

    public MyBadCredentialsException(String message) {
        super(message);
    }
}
