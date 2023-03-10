package com.example.springbootproject.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class AppUser {

    private Integer id;

    private String username;

    private String password;

    private EncryptionAlgorithm algorithm;

    private List<String> authorities;

    private boolean mfa;

    private String optSecretKey;
}
