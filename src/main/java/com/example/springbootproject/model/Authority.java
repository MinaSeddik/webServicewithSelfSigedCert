package com.example.springbootproject.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Authority {

    private Integer id;

    private String name;

}
