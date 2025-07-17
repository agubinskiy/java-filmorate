package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

//Формат рейтинга, который приходит в запросе на добавление/обновление фильма
@Data
public class RateDtoForFilm {
    public Integer id;
}
