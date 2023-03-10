package com.example.springbootproject.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserEntity {

    private Integer id;

    private String username;

    private String password;

    private String algorithm;

    private boolean mfa;

    private String secretKey;

}
