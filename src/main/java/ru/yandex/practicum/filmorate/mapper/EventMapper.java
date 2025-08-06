package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.EventDTO;
import ru.yandex.practicum.filmorate.model.Event;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventMapper {
    public static EventDTO mspToEventDto(Event event) {
        EventDTO dto = new EventDTO();
        dto.setEntityId(event.getEntityId());
        dto.setUserId(event.getUserId());
        dto.setEventType(event.getEventType());
        dto.setOperation(event.getOperation());
        dto.setTimestamp(event.getTimestamp().getTime());
        dto.setEventId(event.getId());

        return dto;
    }
}
