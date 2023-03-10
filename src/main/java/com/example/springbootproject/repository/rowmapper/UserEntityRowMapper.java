package com.example.springbootproject.repository.rowmapper;

import com.example.springbootproject.entity.UserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Predicate;

public class UserEntityRowMapper implements RowMapper<UserEntity> {

    @Override
    public UserEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String algorithm = resultSet.getString("algorithm");
        boolean mfaEnabled = resultSet.getBoolean("mfa_enabled");
        String optSecretKey = resultSet.getString("otp_secret_key");

//        UserEntity e = UserEntityFactory.build(resultSet);



        return UserEntity.builder()
                .id(id)
                .username(username)
                .password(password)
                .algorithm(algorithm)
                .mfa(mfaEnabled)
                .secretKey(optSecretKey)
                .build();

    }
}
