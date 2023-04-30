package com.example.springbootproject.acl;


import lombok.Getter;
import lombok.Setter;

/*

References:
https://stackoverflow.com/questions/16139712/how-to-design-a-hierarchical-role-based-access-control-system?rq=1
https://www.baeldung.com/role-and-privilege-for-spring-security-registration

 */
@Getter
@Setter
public class Permission {

    private String id;
    private String code;
    private String name;
    private String desc;
}
