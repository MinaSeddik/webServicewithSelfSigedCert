package com.example.springbootproject.repository.rowmapper;

import com.example.springbootproject.domain.Book;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookRowMapper implements RowMapper<Book> {

    @Override
    public Book mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String title = resultSet.getString("title");

        return Book.builder()
                .id(id)
                .title(title)
                .build();

    }

}