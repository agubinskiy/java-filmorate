package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

/**
 * Класс для описания формата рейтинга, который приходит в запросе на добавление/обновление фильма
 */
@Data
public class RateDtoForFilm {
    public Integer id;
}
