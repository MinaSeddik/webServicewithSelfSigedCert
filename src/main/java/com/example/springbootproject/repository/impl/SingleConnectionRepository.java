package com.example.springbootproject.repository.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;

@Repository
@Slf4j
@Profile("single-connection")
public class SingleConnectionRepository {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl("jdbc:h2:mem:test");
        config.setUsername("user");
        config.setPassword("password");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    private JdbcTemplate jdbcTemplate;

    public SingleConnectionRepository() throws SQLException {
        Connection connection = ds.getConnection();
        jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(connection, false));
    }

}
