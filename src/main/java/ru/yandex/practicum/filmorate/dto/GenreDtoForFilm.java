package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

/**
 * Класс для описания формата жанра, который приходит в запросе на добавление/обновление фильма
 */
@Data
public class GenreDtoForFilm {
    public Integer id;
}
