package com.example.springbootproject.exception;

import org.springframework.data.relational.core.sql.In;

public class InvalidUserException extends RuntimeException{

    public InvalidUserException(){

    }

    public InvalidUserException(String message){
        super(message);
    }

}
