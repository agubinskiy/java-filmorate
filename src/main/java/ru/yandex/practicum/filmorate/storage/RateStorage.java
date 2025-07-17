package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Rate;

import java.util.Collection;
import java.util.Optional;

public interface RateStorage {
    Collection<Rate> findAllRates();

    Optional<Rate> getRate(Integer id);
}
