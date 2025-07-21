package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Класс для описания формата жанра, который выводим в ответе
 */
@Data
public class GenreDto {
    @NotBlank
    private int id;

    @NotBlank
    private String name;
}
