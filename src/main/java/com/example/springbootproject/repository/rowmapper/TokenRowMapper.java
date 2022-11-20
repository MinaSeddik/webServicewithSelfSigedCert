package com.example.springbootproject.repository.rowmapper;

import com.example.springbootproject.domain.UserEntity;
import com.example.springbootproject.model.Token;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TokenRowMapper implements RowMapper<Token> {

    @Override
    public Token mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String identifier = resultSet.getString("identifier");
        String token = resultSet.getString("token");

        return Token.builder()
                .id(id)
                .identifier(identifier)
                .token(token)
                .build();

    }
}
