package com.example.springbootproject.repository.impl;

import com.example.springbootproject.acl.Role;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepository {


    public Role findByName(String role_user) {

        // .. do sql stmt

        return Role.builder()
                .id(1)
                .name("ROLE_ADMIN")
                .build();
    }
}
