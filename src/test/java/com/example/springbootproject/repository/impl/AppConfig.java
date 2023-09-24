package com.example.springbootproject.repository.impl;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder().setName("test-db").build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public PlatformTransactionManager transactionManager(){
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource());
        return transactionManager;
    }
}