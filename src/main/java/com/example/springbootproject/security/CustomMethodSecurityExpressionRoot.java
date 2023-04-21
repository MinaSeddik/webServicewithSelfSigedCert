package com.example.springbootproject.security;

import com.example.springbootproject.domain.AppUser;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class CustomMethodSecurityExpressionRoot
        extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    /*
    Usage: @PreAuthorize("isMember(#id)")
     */
    public boolean isMember(Long OrganizationId) {
//        User user = ((AppUser) this.getPrincipal()).getUser();
        AppUser appUser = (AppUser) this.getPrincipal();


//        return user.getOrganization().getId().longValue() == OrganizationId.longValue();
        return appUser.getId().longValue() == OrganizationId;
    }

    @Override
    public void setFilterObject(Object filterObject) {

    }

    @Override
    public Object getFilterObject() {
        return null;
    }

    @Override
    public void setReturnObject(Object returnObject) {

    }

    @Override
    public Object getReturnObject() {
        return null;
    }

    @Override
    public Object getThis() {
        return null;
    }

}