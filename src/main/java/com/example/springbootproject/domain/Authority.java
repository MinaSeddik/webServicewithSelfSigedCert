package com.example.springbootproject.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Authority {

    private Integer id;

    private String name;

}
