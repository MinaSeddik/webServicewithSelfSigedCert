package com.example.springbootproject.domain;


import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class Credential {

    @NotNull(message = "{user.firstName.notNull}")
    private String username;

    @NotNull(message = "{user.firstName.notNull}")
    private String password;

}
