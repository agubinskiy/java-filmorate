package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Класс для описания формата рейтинга, который выводим в ответе
 */
@Data
public class RateDto {
    @NotBlank
    public int id;

    @NotBlank
    public String name;
}
