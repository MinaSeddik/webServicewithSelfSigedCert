package com.example.springbootproject.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Comment {

    private Long id;
    private String data;
}
