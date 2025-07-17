package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

//Формат жанра, который приходит в запросе на добавление/обновление фильма
@Data
public class GenreDtoForFilm {
    public Integer id;
}
