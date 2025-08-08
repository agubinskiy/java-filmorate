package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {
    private long id;
    private long entityId;
    private long userId;
    private EventType eventType;
    private OperationType operation;
    private Timestamp timestamp;
}
