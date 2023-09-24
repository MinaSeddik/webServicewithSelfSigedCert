package com.example.springbootproject.repository.batchinsertupdate;

import com.example.springbootproject.domain.User2;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public class UserBatchUpdatePreparedStatementSetter implements BatchPreparedStatementSetter {

    private List<User2> users;

    public UserBatchUpdatePreparedStatementSetter(List<User2> users) {
        super();
        this.users = users;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) {

        try {
            User2 user = users.get(i);
            ps.setInt(1, user.getId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getFirstName());
            ps.setString(5, user.getLastName());
            ps.setString(6, user.getEmail());
            ps.setString(7, user.getPhoneNumber());
            ps.setBoolean(8, user.isActive());
            ps.setBoolean(9, user.isLocked());
            ps.setDate(10, user.getDateOfBirth());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getBatchSize() {
        return users.size();
    }
}