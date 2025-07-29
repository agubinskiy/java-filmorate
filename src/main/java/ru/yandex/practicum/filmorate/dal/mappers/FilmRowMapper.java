package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    private final Map<Long, List<Genre>> genres;
    private final Map<Long, Set<Long>> likes;

    public FilmRowMapper(Map<Long, List<Genre>> genres, Map<Long, Set<Long>> likes) {
        this.genres = genres;
        this.likes = likes;
    }

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        Long filmId = resultSet.getLong("id");
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));


        film.setGenres(genres.getOrDefault(filmId, Collections.emptyList()));
        film.setMpa(Rate.fromId(resultSet.getInt("rate_id")));
        film.setLikes(likes.getOrDefault(filmId, Collections.emptySet()));

        return film;
    }
}
