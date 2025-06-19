package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {
    public Optional<Film> getFilm(Long id);

    public Collection<Film> findAllFilms();

    public Film addFilm(Film film);

    public Film updateFilm(Film film);

    public void deleteFilm(Long id);

    public Map<Long, Film> getFilms();
}
