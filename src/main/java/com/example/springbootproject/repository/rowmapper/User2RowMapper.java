package com.example.springbootproject.repository.rowmapper;

import com.example.springbootproject.domain.User2;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User2RowMapper implements RowMapper<User2> {

    @Override
    public User2 mapRow(ResultSet resultSet, int rowNum) throws SQLException {


        int id = resultSet.getInt("id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String firstName = resultSet.getString("firstName");
        String lastName = resultSet.getString("lastName");
        String email = resultSet.getString("email");
        String phoneNumber = resultSet.getString("phoneNumber");
        boolean active = resultSet.getBoolean("active");
        boolean locked = resultSet.getBoolean("locked");
        Date dob = resultSet.getDate("date_of_birth");


//        Blob picture = resultSet.getBlob("picture");
//        Blob picture = resultSet.getBinaryStream("picture");


        User2 user2 = new User2();
        user2.setId(id);
        user2.setUsername(username);
        user2.setPassword(password);
        user2.setFirstName(firstName);
        user2.setLastName(lastName);
        user2.setEmail(email);
        user2.setPhoneNumber(phoneNumber);
        user2.setActive(active);
        user2.setLocked(locked);
        user2.setDateOfBirth(dob);

        return user2;
    }

}
