package com.example.springbootproject.acl;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*

References:
https://stackoverflow.com/questions/16139712/how-to-design-a-hierarchical-role-based-access-control-system?rq=1
https://www.baeldung.com/role-and-privilege-for-spring-security-registration

 */
@Getter
@Setter
public class User {

    private int id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private boolean active;
    private boolean locked;

    private int role_id;
//    private Role role;

//    or list of roles based on the business requirements
//    private List<Integer> roles;
//    private List<Role> roles;


 private List<Integer> permissions;
// private List<Permission> permissions;

}
