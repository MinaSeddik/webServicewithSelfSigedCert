package com.example.springbootproject.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Token {

    private int id;

    private String identifier;

    private String token;

}
