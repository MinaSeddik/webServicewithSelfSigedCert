package com.example.springbootproject.repository.impl;

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
public class OtpRecoveryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int addRecoveryCodesForUser(int userId, List<String> recoveryCodes) {

        log.debug("Adding Recovery Codes for userId {}, codes: {}", userId, recoveryCodes);

        int[] insertedRows = jdbcTemplate.batchUpdate("INSERT INTO otp_recovery (user_id, code, is_used) VALUES (?, ?, ?)",
                new BatchPreparedStatementSetter() {

                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, userId);
                        ps.setString(2, recoveryCodes.get(i));
                        ps.setBoolean(3, false);
                    }

                    public int getBatchSize() {
                        return recoveryCodes.size();
                    }

                });

        int insertedRowsCount = IntStream.of(insertedRows).sum();
        log.debug("Inserted row(s): {}", insertedRowsCount);

        return insertedRowsCount;
    }


    public List<String> findRecoveryCodesById(int userId) {

        log.info("Find Recovery Codes for userId:{}", userId);

        String sql = "select code from otp_recovery where user_id = ?;";
        List<String> recoveryCodes = jdbcTemplate.queryForList(sql, String.class, userId);

        log.debug("Run query: {} with parameter: {}; Recovery Codes: {}", sql, userId, recoveryCodes);

        return recoveryCodes;
    }
}
