package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.*;
import ru.yandex.practicum.filmorate.dal.mappers.*;
import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

public interface DirectorStorage {
    public List<Director> findAll();

    public Optional<Director> findById(long id);

    public boolean deleteFilmLike(long directorId);

    public Director save(Director director);

    public Director update(Director director);
}
