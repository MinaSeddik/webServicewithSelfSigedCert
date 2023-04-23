package com.example.springbootproject.repository.rowmapper;

import com.example.springbootproject.audit.*;
import com.example.springbootproject.audit.event.Action;
import com.example.springbootproject.entity.AuthorityEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;

public class EventRowMapper implements RowMapper<Event> {

    private final ObjectMapper objectMapper;

//    private EnumSet<Action> actionSet = EnumSet.allOf(Action.class);


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

            EventAction eventAction = objectMapper.readValue(detailsAsJson, actionType.getHandlerClass());
            eventDetail = EventDetail.builder()
                    .eventAction(eventAction)
                    .build();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return Event.builder().eventTime(EventTime.builder().timeCreated(createdAt).build())
                .eventSource(eventSource)
                .eventDetail(eventDetail)
                .build();
    }
}