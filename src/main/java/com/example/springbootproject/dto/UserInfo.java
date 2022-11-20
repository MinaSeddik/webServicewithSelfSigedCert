package com.example.springbootproject.dto;

import com.example.springbootproject.validation.ValidPassword;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfo {

    @NotNull(message = "{user.firstName.notNull}")
    @NotBlank(message = "{user.firstName.notBlank}")
    @Size(min = 1, max = 60, message = "{user.firstName.size}")
    private String firstName;

    @NotNull(message = "{user.lastName.notNull}")
    @NotBlank(message = "{user.lastName.notBlank}")
    @Size(min = 1, max = 60, message = "{user.lastName.size}")
    private String lastName;

    @NotNull(message = "{user.email.notNull}")
    @Size(min = 1, max = 60, message = "{user.email.size}")
    @Email(message = "{user.email.email}")
    private String email;

    @NotNull(message = "{user.username.notNull}")
    @NotBlank(message = "{user.username.notBlank}")
    @Size(min = 1, max = 50, message = "{user.username.size}")
    private String username;

    @NotNull(message = "{user.password.notNull}")
    @NotBlank(message = "{user.password.notBlank}")
    @ValidPassword
    private String password;

    private boolean mfa;

    private String optSecretKey;

    private String qrURI;

    private List<String> optRecoveryCodes;

}
