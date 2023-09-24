package com.example.springbootproject.repository.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Repository
@Slf4j
public class EmailQueueRepository {


    /*
        CREATE TABLE unsent_emails (
         id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
         -- columns for the message, from, to, subject, etc.
         status ENUM('unsent', 'claimed', 'sent'),
         owner INT UNSIGNED NOT NULL DEFAULT 0,
         ts TIMESTAMP,
         KEY (owner, status, ts)
        );

     */

    @Autowired
    private JdbcTemplate jdbcTemplate;


    //    https://stackoverflow.com/questions/22242081/select-for-update-holding-entire-table-in-mysql-rather-than-row-by-row
    @Transactional
    public void getUnsentEmail(int workerId /* could be used instead of connection Id*/) throws SQLException {

        // Method (1)
        /*
        BEGIN;

        -- there is no index, will lock the whole tab;e while transaction is committed (or rolled-back)
        SELECT id FROM unsent_emails WHERE owner = 0 AND status = 'unsent' LIMIT 10 FOR UPDATE;
        -- result: 123, 456, 789


        UPDATE unsent_emails SET status = 'claimed', owner = CONNECTION_ID() WHERE id IN(123, 456, 789);


        COMMIT;
        */

//=================================================================================================

        // Method (2) - better one
        // here is a better way to do it

        /*

        -- db index is created perfectly
        UPDATE unsent_emails SET status = 'claimed', owner = CONNECTION_ID() WHERE owner = 0 AND status = 'unsent' LIMIT 10;
        -- get the count of changed rows

        SELECT id FROM unsent_emails WHERE owner = CONNECTION_ID() AND status = 'claimed';
        -- resul: 123, 456, 789

         */


//=================================================================================================
        // clean up rows that were claimed but never processed
        // 10, 20, 30 are SHOW PROCESSLIST current thread IDs
        /*
            UPDATE unsent_emails SET owner = 0, status = 'unsent'
             WHERE owner NOT IN(0, 10, 20, 30) AND status = 'claimed'
             AND ts < CURRENT_TIMESTAMP - INTERVAL 10 MINUTE;
         */
    }


}
