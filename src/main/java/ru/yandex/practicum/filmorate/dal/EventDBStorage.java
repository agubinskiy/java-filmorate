package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.util.List;

@Repository
public class EventDBStorage extends BaseDbStorage<Event> implements EventStorage {

    private static final String GET_USER_FEED = "SELECT * FROM Events WHERE user_id = ?";

    private static final String ADD_EVENT = "INSERT INTO Events(entity_id, user_id, event_type, operation, " +
            "create_time) VALUES(?, ?, ?, ?, ?)";

    public EventDBStorage(JdbcTemplate jdbc) {
        super(jdbc);
    }

    @Override
    public Event addEvent(Event event) {
        long id = insert(
                ADD_EVENT,
                event.getEntityId(),
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getTimestamp()
        );
        event.setId(id);
        return event;
    }

    @Override
    public List<Event> getFeed(Long userId) {
        RowMapper<Event> mapper = new EventRowMapper();
        return findMany(GET_USER_FEED, mapper, userId);
    }

}
