package com.example.springbootproject.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

// User Decorator class
public class SecurityUserDetails implements UserDetails {
    private final AppUser user;

    public SecurityUserDetails(AppUser user) {
        this.user = user;
    }

    public final AppUser getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean is2faEnabled(){
        return user.isMfa();
    }

    public String getOptSecretKey(){
        return user.getOptSecretKey();
    }

    public int getUserId(){
        return user.getId();
    }

    public int getFailedAttempts(){
        // get it from the database
        return 0;
    }

    public Date getLockedTime(){
        // get it from the database
        return new Date();
    }

    public void setAccountNonLocked(boolean b) {
    }

    public void setFailedAttempts(int i) {
    }

    public void setLockedTime(Object o) {
    }
}