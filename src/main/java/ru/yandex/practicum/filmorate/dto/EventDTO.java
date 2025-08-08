package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;

@Data
public class EventDTO {
    @NotBlank
    private long eventId;
    @NotBlank
    private long entityId;
    @NotBlank
    private long userId;
    @NotBlank
    private EventType eventType;
    @NotBlank
    private OperationType operation;
    @NotBlank
    private long timestamp;
}
