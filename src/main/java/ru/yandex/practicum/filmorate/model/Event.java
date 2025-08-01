package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.EventType;
import ru.yandex.practicum.filmorate.dto.OperationType;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {
    @NotBlank
    private long id;
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
