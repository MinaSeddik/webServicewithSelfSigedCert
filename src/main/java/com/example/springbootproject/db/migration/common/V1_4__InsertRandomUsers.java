package com.example.springbootproject.db.migration.common;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;

import java.sql.SQLException;

public class V1_4__InsertRandomUsers extends BaseJavaMigration {

    @Override
    public void migrate(Context context) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(context.getConnection());

        try {
            jdbcTemplate.execute("select * from USERS");
        } catch (SQLException e) {
            System.out.println("******* " + e);
        }

        // Create 10 random users
//        for (int i = 1; i <= 10; i++) {
//            jdbcTemplate.execute(String.format("insert into test_user"
//                    + " (username, first_name, last_name) values"
//                    + " ('%d@reflectoring.io', 'Elvis_%d', 'Presley_%d')", i, i, i));            jdbcTemplate.execute(String.format("insert into test_user"
//                    + " (username, first_name, last_name) values"
//                    + " ('%d@reflectoring.io', 'Elvis_%d', 'Presley_%d')", i, i, i));
//        }
    }
}