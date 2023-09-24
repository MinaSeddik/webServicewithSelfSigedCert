package com.example.springbootproject.repository.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
@Slf4j
public class InsertAndGetIdRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public Long save(final String data, InputStream inputStream) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into sometable (column) values (?)";


        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {

                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, data);

                // can we set blob
//                ps.setBlob(2, (Blob) null);
                ps.setBlob(3, (InputStream) inputStream);
                return ps;
            }
        }, keyHolder);


        Long newId = keyHolder.getKey().longValue();
        return newId;
    }
}
