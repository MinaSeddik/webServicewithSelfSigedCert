package com.example.springbootproject.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Token {

    private int id;

    private String identifier;

    private String token;

}
