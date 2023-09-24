package com.example.springbootproject.repository.batchinsertupdate;

import com.example.springbootproject.domain.User2;

import java.util.List;
import java.util.stream.Collectors;

/*
    UPDATE user2
            SET USERNAME = CASE
            WHEN ID=1 THEN 'PETERM'
            WHEN ID=2 THEN 'MARTIN'
            WHEN ID=3 THEN 'ASHJA'
            END,

            PASSWORD = CASE
            WHEN ID=1 THEN 'ABC123abc*'
            WHEN ID=2 THEN 'password'
            WHEN ID=3 THEN 'Password123'
            END

            WHERE ID IN ( 1,2,3);
*/

//Reference: https://javabydeveloper.com/spring-jdbctemplate-batch-update-with-maxperformance/

public class UserBatchQueryBuilder {

    public static String buildUpdateQuery(List<User2> users) {

        StringBuffer query = new StringBuffer("UPDATE user2 SET USERNAME = CASE ");
        StringBuffer setUserName = new StringBuffer();
        StringBuffer setUpdatedTime = new StringBuffer();

        users.forEach(u -> {
            setUserName.append(String.format("WHEN ID=%d THEN '%s' ", u.getId(), u.getUsername()));
            setUpdatedTime.append(String.format("WHEN ID=%d THEN NOW() ", u.getId()));
        });

        query.append(setUserName)
                .append("END, PASSWORD = CASE ")
                .append(setUpdatedTime)
                .append(String.format("END WHERE ID IN (%s)",
                        String.join(",", users.stream().map(u -> u.getId()+"").collect(Collectors.toList()))));

        return query.toString();
    }
}