package ru.yandex.practicum.filmorate.model.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SearchBy {
    DIRECTOR("director"),
    TITLE("title"),
    DIRECTOR_AND_TITLE("director,title"),
    TITLE_AND_DIRECTOR("title,director");;

    private final String value;

    SearchBy(String value) {
        this.value = value;
    }

    public static SearchBy fromString(String value) {
        return Arrays.stream(values())
                .filter(searchBy -> searchBy.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный параметр поиска"));
    }
}
