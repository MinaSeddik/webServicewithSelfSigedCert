package com.example.springbootproject.repository.rowmapper;

import com.example.springbootproject.audit.Event;
import com.example.springbootproject.audit.EventDetail;
import com.example.springbootproject.audit.EventSource;
import com.example.springbootproject.audit.EventTime;
import com.example.springbootproject.audit.event.Action;
import com.example.springbootproject.entity.AuthorityEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class EventRowMapper implements RowMapper<Event> {

    private final ObjectMapper objectMapper;

    public EventRowMapper(ObjectMapper mapper) {
        this.objectMapper = mapper;
    }

    @Override
    public Event mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        Action actionType = Action.valueOf(resultSet.getString("action_type"));

        Timestamp ts = resultSet.getTimestamp("created_at");
        Date createdAt = new Date(ts.getTime());

        String userName = resultSet.getString("username");

        String sourceAsJson = resultSet.getString("source");
        String detailsAsJson = resultSet.getString("details");

        EventSource eventSource;
        EventDetail eventDetail;

        try {
            eventSource = objectMapper.readValue(sourceAsJson, EventSource.class);
            eventDetail = objectMapper.readValue(detailsAsJson, EventDetail.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return Event.builder().eventTime(EventTime.builder().timeCreated(createdAt).build())
                .eventSource(eventSource)
                .eventDetail(eventDetail)
                .build();
    }
}