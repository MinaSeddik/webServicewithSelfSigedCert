package com.example.springbootproject.repository.impl;

import com.example.springbootproject.model.Token;
import com.example.springbootproject.repository.rowmapper.TokenRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class MyAppCsrfTokenRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Optional<Token> findTokenByIdentifier(String identifier) {
        log.debug("Find CSRF token by identifier:{}", identifier);

        String sql = "select * from token where identifier = ?;";

        /*
            In JdbcTemplate , queryForInt, queryForLong, queryForObject all such methods expects that executed query will return one and only one row. If you get no rows or more than one row that will result in IncorrectResultSizeDataAccessException
         */
        // should return one and only one single raw
//        Token token = jdbcTemplate.queryForObject(sql, new TokenRowMapper(), identifier);

        List<Token> tokenList = jdbcTemplate.query(sql, new TokenRowMapper(), identifier);


        if (tokenList.isEmpty()) {
            Optional<Token> tokenOptional = Optional.ofNullable(null);
            log.debug("Run query: {} with parameter: {}; NOT exists: {}", sql, identifier, tokenOptional.isPresent());

            return tokenOptional;
        } else if (tokenList.size() == 1) { // list contains exactly 1 element
            Optional<Token> tokenOptional = Optional.ofNullable(tokenList.get(0));
            log.debug("Run query: {} with parameter: {}; Exists: {}", sql, identifier, tokenOptional.isPresent());

            return tokenOptional;
        }

        // list contains more than 1 element
        Optional<Token> tokenOptional = Optional.ofNullable(tokenList.get(0));
        log.debug("Run query: {} with parameter: {}; Many rows Exists: {}", sql, identifier, tokenList.size());

        return tokenOptional;


    }

    public int save(Token token) {

        log.debug("Save CSRF token for identifier:{}", token.getIdentifier());
        String sql = "INSERT IGNORE INTO token (id, identifier, token) values(NULL, '" + token.getIdentifier() + "' ,'" + token.getToken() + "')";

        int insertedRows = jdbcTemplate.update(sql);
        log.debug("Inserted row(s): {}", insertedRows);

        return insertedRows;
    }
}
