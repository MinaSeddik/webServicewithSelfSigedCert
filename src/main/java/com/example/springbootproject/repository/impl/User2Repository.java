package com.example.springbootproject.repository.impl;

import com.example.springbootproject.domain.BankAccount;
import com.example.springbootproject.domain.User2;
import com.example.springbootproject.repository.rowmapper.BankAccountRowMapper;
import com.example.springbootproject.repository.rowmapper.User2RowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class User2Repository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<User2> getAllUsers() {

        log.info("getAllUsers Repo called");
        List<User2> list =  jdbcTemplate.query("select * from user2", new User2RowMapper());

        log.info("getAllUsers retrieved: {}", list.size());

        return list;
    }


}
