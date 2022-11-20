package com.example.springbootproject.repository.impl;


import com.example.springbootproject.repository.JwtBlacklistRepository;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Profile("jwt-db")
@Repository
@Slf4j
public class JwtDatabaseBlacklistRepository implements JwtBlacklistRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void blacklist(String user, String token) {

        log.debug("Blacklist a JWT token: {} for user: {}", token, user);
        String sql = "INSERT IGNORE INTO jwt_blacklist (id, username, token) values(NULL, '" +
                user + "' ,'" + token + "')";

        int insertedRows = jdbcTemplate.update(sql);
        log.info("Inserted row(s): {}", insertedRows);
    }

    public boolean isTokenBlacklisted(String user, String token) {

        log.info("Find token: {} for user: {}", token, user);

        String sql = "select token from jwt_blacklist where username = ?";
        String blacklistedToken = jdbcTemplate.queryForObject(sql, String.class, user);

        return StringUtils.isNotBlank(blacklistedToken);
    }

}
