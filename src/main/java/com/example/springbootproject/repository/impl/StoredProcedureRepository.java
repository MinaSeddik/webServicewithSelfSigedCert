package com.example.springbootproject.repository.impl;

import com.example.springbootproject.domain.User2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/*

Reference:
https://javabydeveloper.com/spring-jdbctemplate-call-stored-procedures-functions/

mysql> delimiter //

mysql> CREATE PROCEDURE simpleproc2 (IN s CHAR(100))
    -> BEGIN
    -> SELECT n.*, CONCAT('Hello, ', s, '!') FROM names n;
    -> END//

mysql> delimiter ;

 */
@Repository
@Slf4j
public class StoredProcedureRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<User2> callStoredProcedure() {

        log.info("callStoredProcedure Repo called");
        String param1 = "World";
//        int outputInteger =  jdbcTemplate.update("call simpleproc2 (?)", param1);

//        Object params[]={param1};
        List<SqlParameter> parameters = Arrays.asList(new SqlParameter(Types.NVARCHAR));

        Map<String, Object> out = jdbcTemplate.call(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                CallableStatement cs = con.prepareCall("{call simpleproc2(?)}");
                cs.setString(1, param1);
                return cs;
            }
        }, parameters);

        log.info("out: " + out);
        log.info("out: " + out.keySet().stream().collect(Collectors.joining()));
        log.info("out: " + out.values().stream().collect(Collectors.toList()));

        int resultSetPosition = 1;
        List<Map<String, Object>> results = (List<Map<String, Object>>) out.get("#result-set-" + resultSetPosition);

        return null;
    }

    // resultSetPosition = 1 is the result set #1
    public List<User2> mapUsers(Map<String, Object> out, int resultSetPosition) {
        List<User2> users = new ArrayList<User2>();
        List<Map<String, Object>> results = (List<Map<String, Object>>) out.get("#result-set-" + resultSetPosition);
        results.forEach(u -> {
            User2 user = new User2();
            user.setId((int) u.get("Id"));
//            user.setUserName((String) u.get("USERNAME"));
//            user.setPassword((String) u.get("PASSWORD"));
//            user.setCreatedTime((Date) u.get("CREATEDTIME"));
//            if (u.get("UPDATEDTIME") != null)
//                user.setCreatedTime((Date) u.get("UPDATEDTIME"));
//            user.setUserType(UserType.valueOf((String) u.get("USERTYPE")));
//            user.setDateofBirth((Date) u.get("DATEOFBIRTH"));
            users.add(user);
        });
        return users;
    }



    /* Stored procedure to get search count for paged users by name
            DROP procedure IF EXISTS `count_search_users_proc`$

            CREATE PROCEDURE `count_search_users_proc` (
            IN  order_by VARCHAR(50),
            IN  direction VARCHAR(4),
            IN  limit_ INT,
            IN  offset_ INT,
            OUT total INT
            )
            BEGIN
            SELECT COUNT(id)
            INTO total
            FROM USER;
            SELECT * from USER ORDER BY CONCAT(order_by,' ', direction) LIMIT limit_ OFFSET offset_;
            END$
     */


    /* Calling Stored Procedure using SimpleJdbcCall */
    public Map<String, Object> searchUsersWithCount(String by, String direction, int size, int offset) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("count_search_users_proc")
                .declareParameters(
                        new SqlParameter("order_by", Types.NVARCHAR),
                        new SqlParameter("direction", Types.VARCHAR),
                        new SqlParameter("limit_", Types.INTEGER),
                        new SqlParameter("offset_", Types.INTEGER),
                        new SqlOutParameter("total", Types.INTEGER));


        Map<String, Object> out = simpleJdbcCall.execute(
                new MapSqlParameterSource("order_by", by)
                        .addValue("direction", direction)
                        .addValue("limit_", size)
                        .addValue("offset_", offset));


        // #result-set-1
        List<User2> users = mapUsers(out, 1);

        // out param
        int total = (Integer) out.get("total");


        return out;
    }
}
