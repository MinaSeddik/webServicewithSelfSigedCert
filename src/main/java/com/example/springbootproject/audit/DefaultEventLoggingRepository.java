package com.example.springbootproject.audit;

import com.example.springbootproject.repository.rowmapper.EventRowMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Repository
@Slf4j
public class DefaultEventLoggingRepository implements EventLoggingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper mapper;


    @Override
    public void save(Event event) {

        String actionType = event.getEventDetail().getEventAction().actionType().name();
        Date createAt = event.getEventTime().getTimeCreated();
        String userName = event.getEventSource().getUser().getUsername();

        String eventSourceAsJson;
        String eventDetailsAsJson;

        try {
            eventSourceAsJson = mapper.writeValueAsString(event.getEventSource());
            eventDetailsAsJson = mapper.writeValueAsString(event.getEventDetail());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        log.info("Insert event record to Audit - of type: {}", actionType);

        // save event to the database
        int insertedRow = jdbcTemplate.update("INSERT INTO event (action_type, created_at, username, source, details) VALUES(?, ?, ?, ?, ? )",
                actionType, new Timestamp(createAt.getTime()), userName, eventSourceAsJson, eventDetailsAsJson);


        log.info("Inserted row(s): {}", insertedRow);

    }

    @Override
    public List<Event> getAllRecords() {


        String sql = "select * from event";
        List<Event> events = jdbcTemplate.query(sql, new EventRowMapper(mapper));

        log.info("Run query: {} ; Events size: {}", sql, events.size());

        return events;

    }
}
