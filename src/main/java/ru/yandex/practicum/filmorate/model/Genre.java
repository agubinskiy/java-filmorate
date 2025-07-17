package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Genre {
    COMEDY(1, "Комедия"),
    DRAMA(2, "Драма"),
    ANIMATION(3, "Мультфильм"),
    THRILLER(4, "Триллер"),
    DOCUMENTARY(5, "Документальный"),
    ACTION(6, "Боевик");

    private final int id;
    private final String displayName;

    @Override
    public String toString() {
        return displayName;
    }

    public static Genre fromId(int id) {
        return Arrays.stream(values())
                .filter(genre -> genre.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный id жанра: " + id));
    }
}
