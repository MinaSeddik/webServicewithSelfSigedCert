package com.example.springbootproject.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
@EnableR2dbcRepositories
@EnableTransactionManagement
class R2DBCConfig /*extends AbstractR2dbcConfiguration */ {

//    @Bean
//    public MySqlConnectionFactory connectionFactory() {
//        return new MySqlConnectionFactory(
//                MySqlConnectionConfiguration.builder()
//                        .url("mem:testdb;DB_CLOSE_DELAY=-1;")
//                        .username("sa")
//                        .build()
//        );
//    }

    @Bean
    public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

    @Bean
    public TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }


}