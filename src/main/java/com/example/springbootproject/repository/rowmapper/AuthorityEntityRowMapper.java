package com.example.springbootproject.repository.rowmapper;

import com.example.springbootproject.entity.AuthorityEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorityEntityRowMapper implements RowMapper<AuthorityEntity> {

    @Override
    public AuthorityEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        int user = resultSet.getInt("user");

        return AuthorityEntity.builder()
                .id(id)
                .name(name)
                .user(user)
                .build();

    }
}