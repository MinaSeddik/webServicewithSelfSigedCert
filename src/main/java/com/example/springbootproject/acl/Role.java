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
@Builder
@Getter
@Setter
public class Role {

    private int id;
    private String code;
    private String name;
    private String desc;

    private int parentId; // in case of hierarchical roles structure
//    private Role parentRole;

//    private List<Integer> permissions;
    private List<Permission> permissions;

}
