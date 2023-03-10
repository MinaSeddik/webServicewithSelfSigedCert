package com.example.springbootproject.repository.impl;

import com.example.springbootproject.domain.BigRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class ImperativeBigDataRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<BigRecord> fetchAllRecords() {

        List<BigRecord> allData = jdbcTemplate.query("select * from big_data;", new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {

            }
        }, new ResultSetExtractor<List<BigRecord>>() {

            @Override
            public List<BigRecord> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                System.out.println("resultSet.getFetchSize(): " + resultSet.getFetchSize());

                List<BigRecord> list = new ArrayList<>();

                while (resultSet.next()) {

                    String Series_reference = resultSet.getString("Series_reference");
                    Double Period = resultSet.getDouble("Period");
                    String Data_value = resultSet.getString("Data_value");
                    String Suppressed = resultSet.getString("Suppressed");
                    String STATUS = resultSet.getString("STATUS");
                    String UNITS = resultSet.getString("UNITS");
                    Integer MAGNTUDE = resultSet.getInt("MAGNTUDE");
                    String Subject = resultSet.getString("Subject");
                    String Grp = resultSet.getString("Grp");
                    String Series_title_1 = resultSet.getString("Series_title_1");


                    list.add(BigRecord.builder()
                            .Series_reference(Series_reference)
                            .Period(Period)
                            .Data_value(Data_value)
                            .Suppressed(Suppressed)
                            .STATUS(STATUS)
                            .UNITS(UNITS)
                            .MAGNTUDE(MAGNTUDE)
                            .Subject(Subject)
                            .Grp(Grp)
                            .Series_title_1(Series_title_1)
                            .build());

                } // end resultset loop

                return list;
            }
        });

        return allData;
    }

}
