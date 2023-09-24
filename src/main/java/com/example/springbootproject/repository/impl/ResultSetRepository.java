package com.example.springbootproject.repository.impl;

import com.example.springbootproject.domain.User2;
import com.example.springbootproject.repository.rowmapper.User2RowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.*;
import java.util.List;

public class ResultSetRepository {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<User2> getAllUsers() {
        jdbcTemplate.query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                return con.prepareStatement("SELECT id FROM user2", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            }
        }, new ResultSetExtractor<Void>() {
            @Override
            public Void extractData(ResultSet result) throws SQLException, DataAccessException {

//                - first(): moves the cursor to the first row.
//                - next(): moves the cursor forward one row from its current position.
//                - previous(): moves the cursor to the previous row.
//                - relative(int rows): moves the cursor a relative number of rows from its current position. The value of rows can be positive (move forward) or negative (move backward).
//                - absolute(int row): moves the cursor to the given row number. The value of row can be positive or negative. A positive number indicates the row number counting from the beginning of the result set. A negative number indicates the row number counting from the end of the result set.


                result.first();
                result.relative(3);
                result.previous();
                result.absolute(4);
                result.last();
                result.relative(-2);

                return null;

            }
        });

        return null;
    }




}
