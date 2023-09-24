package com.example.springbootproject.repository.batchinsertupdate;

import com.example.springbootproject.domain.User2;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UserBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    private List<User2> users;

    public UserBatchPreparedStatementSetter(List<User2> users) {
        super();
        this.users = users;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) {

        try {
            User2 user = users.get(i);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFirstName());
            ps.setString(4, user.getLastName());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhoneNumber());
            ps.setBoolean(7, user.isActive());
            ps.setBoolean(8, user.isLocked());
            ps.setDate(9, user.getDateOfBirth());


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getBatchSize() {
        return users.size();
    }
}