package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Rate {
    G(1, "G"), //у фильма нет возрастных ограничений,
    PG(2, "PG"), //детям рекомендуется смотреть фильм с родителями,
    PG13(3, "PG-13"), //детям до 13 лет просмотр не желателен,
    R(4, "R"), //лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
    NC17(5, "NC-17"); //лицам до 18 лет просмотр запрещён.

    private final int id;
    private final String displayName;

    @Override
    public String toString() {
        return displayName;
    }

    public static Rate fromId(int id) {
        return Arrays.stream(values())
                .filter(rate -> rate.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный id рейтинга: " + id));
    }
}
