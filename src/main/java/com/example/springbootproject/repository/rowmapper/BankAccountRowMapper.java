package com.example.springbootproject.repository.rowmapper;

import com.example.springbootproject.model.BankAccount;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BankAccountRowMapper implements RowMapper<BankAccount> {

    @Override
    public BankAccount mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String first_name = resultSet.getString("first_name");
        int balance = resultSet.getInt("balance");
        int branch_id = resultSet.getInt("branch_id");

        return BankAccount.builder()
                .id(id)
                .firstName(first_name)
                .balance(balance)
                .branchId(branch_id)
                .build();

    }

}