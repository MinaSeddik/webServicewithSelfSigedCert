package com.example.springbootproject.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorityEntity {

    private Integer id;

    private String name;

    private Integer user;

}
