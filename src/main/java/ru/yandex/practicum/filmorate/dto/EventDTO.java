package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EventDTO {
    @NotBlank
    private String eventId;
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
