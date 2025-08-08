package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

public interface DirectorStorage {
    List<Director> findAll();

    Optional<Director> findById(long id);

    List<Director> findByIds(List<Long> ids);

    boolean deleteFilmDirector(long directorId);

    Director save(Director director);

    Director update(Director director);
}
