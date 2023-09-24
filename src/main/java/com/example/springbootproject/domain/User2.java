package com.example.springbootproject.domain;

import com.example.springbootproject.acl.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class User2 extends User {

    private Date dateOfBirth;
    private Date createdAt;
    private Date updatedAt;

}
