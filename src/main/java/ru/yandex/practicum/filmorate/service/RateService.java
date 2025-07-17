package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.RateDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.RateMapper;
import ru.yandex.practicum.filmorate.storage.RateStorage;

import java.util.List;

@Service
@Slf4j
public class RateService {
    private final RateStorage rateStorage;

    @Autowired
    public RateService(RateStorage rateStorage) {
        this.rateStorage = rateStorage;
    }

    public List<RateDto> findAll() {
        return rateStorage.findAllRates()
                .stream()
                .map(RateMapper::mapToRateDto)
                .toList();
    }

    public RateDto getRate(Integer id) {
        return rateStorage.getRate(id)
                .map(RateMapper::mapToRateDto)
                .orElseThrow(() -> {
                    log.warn("Ошибка при поиске рейтинга. Рейтинга с id={} не существует", id);
                    return new NotFoundException("Не найден рейтинг с id=" + id);
                });
    }
}
