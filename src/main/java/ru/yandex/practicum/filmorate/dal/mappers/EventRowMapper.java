package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dto.EventType;
import ru.yandex.practicum.filmorate.dto.OperationType;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;


public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();
        event.setEventType(EventType.valueOf(rs.getString("event_type")));
        event.setOperation(OperationType.valueOf(rs.getString("operation")));
        event.setEntityId(rs.getInt("entity_id"));
        event.setUserId(rs.getInt("user_id"));
        event.setTimestamp(rs.getLong("create_time"));
        event.setId(rs.getLong("id"));
        return event;
    }
}
