package com.example.springbootproject.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface JwtBlacklistRepository {

    void blacklist(String user, String token);

    boolean isTokenBlacklisted(String user, String token);

}
