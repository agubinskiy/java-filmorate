package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.RateDto;
import ru.yandex.practicum.filmorate.dto.RateDtoForFilm;
import ru.yandex.practicum.filmorate.model.Rate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RateMapper {
    public static RateDto mapToRateDto(Rate rate) {
        RateDto dto = new RateDto();
        dto.setId(rate.getId());
        dto.setName(rate.getDisplayName());
        return dto;
    }

    public static RateDtoForFilm mapToRateDtoForFilm(Rate rate) {
        RateDtoForFilm dto = new RateDtoForFilm();
        dto.setId(rate.getId());
        return dto;
    }
}
