package com.example.springbootproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Profile("reactive")
//@Configuration
//@EnableR2dbcRepositories
//@EnableTransactionManagement
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

//    @Bean
//    public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
//        return new R2dbcTransactionManager(connectionFactory);
//    }
//
//    @Bean
//    public TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
//        return TransactionalOperator.create(transactionManager);
//    }


}