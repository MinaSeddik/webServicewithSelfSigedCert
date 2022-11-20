package com.example.springbootproject.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
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
