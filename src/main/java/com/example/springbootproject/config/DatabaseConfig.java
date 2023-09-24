package com.example.springbootproject.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@ConfigurationProperties("spring.datasource")
@EnableTransactionManagement
// Adding @EnableTransactionManagement annotation creates a PlatformTransactionManager for you
public class DatabaseConfig extends HikariConfig {

    @Bean
    public DataSource dataSource() {
        return new HikariDataSource(this);
    }

    /*
    @Bean
//    @ConditionalOnMissingBean(PlatformTransactionManager.class)
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(dataSource());
    }
*/
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

}
