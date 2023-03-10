package com.example.springbootproject.repository.impl;

import com.example.springbootproject.entity.AuthorityEntity;
import com.example.springbootproject.repository.rowmapper.AuthorityEntityRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.IntStream;

@Repository
@Slf4j
public class AuthorityRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<AuthorityEntity> findAuthoritiesOfUser(int userId) {
        log.info("Find Authorities for userId:{}", userId);

        String sql = "select * from authority where user = ?;";
        List<AuthorityEntity> authorityEntityList = jdbcTemplate.query(sql, new AuthorityEntityRowMapper(), userId);

        log.debug("Run query: {} with parameter: {}; Authorities: {}", sql, userId, authorityEntityList);

        return authorityEntityList;
    }

    public int addAuthorities(List<AuthorityEntity> authorityEntities) {

        log.debug("Create Authorities record for a user, Authorities: {}", authorityEntities);

//        https://mkyong.com/spring/spring-jdbctemplate-batchupdate-example/
//        https://www.concretepage.com/spring/spring-jdbctemplate-batchupdate
//        If the batch is too big, we can split it by a smaller batch size.
//        jdbcTemplate.batchUpdate("INSERT INTO authority (name, user) VALUES (?, ?)",
//                authorityEntities,
//                100,
//                (PreparedStatement ps, AuthorityEntity authorityEntity) -> {
//                    ps.setString(1, authorityEntity.getName());
//                    ps.setInt(2, authorityEntity.getUser());
//                });


        int[] insertedRows = jdbcTemplate.batchUpdate("INSERT INTO authority (name, user) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {

                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, authorityEntities.get(i).getName());
                        ps.setInt(2, authorityEntities.get(i).getUser());
                    }

                    public int getBatchSize() {
                        return authorityEntities.size();
                    }

                });

        int insertedRowsCount = IntStream.of(insertedRows).sum();
        log.debug("Inserted row(s): {}", insertedRowsCount);

        return insertedRowsCount;
    }


}
