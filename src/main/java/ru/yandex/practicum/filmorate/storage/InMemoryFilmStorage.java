package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;


import java.util.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;


@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    @Getter
    private final Map<Long, Film> films = new HashMap<>();
    private long counter;
    private static final Comparator<Film> filmLikesComparator = Comparator.comparingInt((Film film) ->
            film.getLikes().size()).reversed();

    @Override
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(++counter);
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        log.info("Фильм успешно обновлён {}", film);
        return film;
    }

    public Optional<Film> getFilm(Long filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    public List<Film> getMostLikedFilms(int count) {
        return findAllFilms().stream()
                .sorted(filmLikesComparator)
                .limit(count)
                .toList();
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        Film film = getFilm(filmId).orElseThrow();
        film.getLikes().add(userId);
        return film;
    }

    @Override
    public void deleteFilm(Long filmId) {

    }

    @Override
    public List<Film> getMostLikedFilmsByGenreYear(int count, long genreId, int year) {
        return null;
    }

    @Override
    public List<Film> getMostLikedFilmsByGenre(int count, long genreId) {
        return null;
    }

    @Override
    public List<Film> getMostLikedFilmsByYear(int count, int year) {
        return null;
    }

    @Override

    public void saveFilmDirectors(long filmId, List<Director> directors){
    }

    @Override
    public List<Film> getFilmsByIdDirector(long directorId) {
        return new ArrayList<>();
    }

    @Override
    public void updateFilmDirectors(Long filmId, List<Director> directors) {

    }

    @Override
    public Map<Long, Map<Long, Double>> getAllLikes() {
        Map<Long, Map<Long, Double>> result = new HashMap<>();
        for (Film film : films.values()) {
            Set<Long> likes = film.getLikes();
            if (likes != null) {
                for (Long userId : likes) {
                    result.computeIfAbsent(userId, k -> new HashMap<>()).put(film.getId(), 1.0);
                }
            }
        }
        return result;
    }

    private List<Long> getUserLikes(Long userId) {
        return films.entrySet().stream()
                .filter(entry -> entry.getValue().getLikes().contains(userId))
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        //копируем список фильмов пользователя
        List<Long> commonFilms = new ArrayList<>(getUserLikes(userId));
        //оставляем только пересечения со списком фильмов друга
        commonFilms.retainAll(getUserLikes(friendId));
        return null;
    }

    @Override
    public void removeLike(Long userId, Long filmId) {

    }
}
