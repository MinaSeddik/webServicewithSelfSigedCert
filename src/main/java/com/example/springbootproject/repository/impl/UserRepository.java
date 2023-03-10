package com.example.springbootproject.repository.impl;

import com.example.springbootproject.entity.UserEntity;
import com.example.springbootproject.repository.rowmapper.UserEntityRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Optional<UserEntity> findUserByUsername(String userName) {
        log.info("Find user by username:{}", userName);

        String sql = "select * from user where username = ?;";
//        UserEntity userEntity = jdbcTemplate.queryForObject(sql, new UserEntityRowMapper(), userName);

        List<UserEntity> userEntityList = jdbcTemplate.query(sql, new UserEntityRowMapper(), userName);

        if (userEntityList.isEmpty()) {
            Optional<UserEntity> userEntityOptional = Optional.ofNullable(null);
            log.debug("Run query: {} with parameter: {}; User does NOT exist", sql, userName);

            return userEntityOptional;
        } else if (userEntityList.size() == 1) { // list contains exactly 1 user
            Optional<UserEntity> userEntityOptional = Optional.ofNullable(userEntityList.get(0));
            log.debug("Run query: {} with parameter: {}; Exists: {}", sql, userName, userEntityOptional.isPresent());

            return userEntityOptional;
        }

        // list contains more than 1 user
        log.debug("Run query: {} with parameter: {}; Many rows Exists: {}", sql, userName, userEntityList.size());
        return Optional.ofNullable(null);

    }

    public int addUser(UserEntity userEntity) {

        String algo = "BCRYPT";
        log.debug("Create new user {}", userEntity.getUsername());

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

//        String sql = "INSERT IGNORE INTO user (id, username, password, algorithm, mfa_enabled, otp_secret_key) values(NULL, '" +
//                userEntity.getUsername() + "' ,'" + userEntity.getPassword() + "', '" + algo + "," +
//                userEntity.isMfa() + ",'" + userEntity.getSecretKey() + "')";

        String sql = "INSERT IGNORE INTO user (username, password, algorithm, mfa_enabled, otp_secret_key) values(?, ?, ?, ?, ?)";

//        int insertedRows = jdbcTemplate.update(sql);
        int insertedRows = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, userEntity.getUsername());
            ps.setString(2, userEntity.getPassword());
            ps.setString(3, algo);
            ps.setBoolean(4, userEntity.isMfa());
            ps.setString(5, userEntity.getSecretKey());
            return ps;
        }, keyHolder);

        log.debug("Run query: {} with parameter: {}; User does NOT exist", sql, userEntity.getUsername());

        log.info("Inserted row(s): {}, userId={}", insertedRows, keyHolder.getKey().intValue());

        return keyHolder.getKey().intValue();
    }

}
