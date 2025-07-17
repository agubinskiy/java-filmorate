package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    private final JdbcTemplate jbdc;

    public FilmRowMapper(JdbcTemplate jbdc) {
        this.jbdc = jbdc;
    }

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));

        List<Genre> genres = jbdc.query(
                "SELECT genre_id FROM FilmGenres WHERE film_id = ?",
                (rs, rn) -> Genre.fromId(rs.getInt("genre_id")), film.getId()
        );
        film.setGenres(genres);

        Rate rate = Rate.fromId(jbdc.queryForObject(
                "SELECT rate_id FROM Films WHERE id = ?",
                Integer.class, film.getId()
        ));
        film.setMpa(rate);

        Set<Long> likes = new HashSet<>(jbdc.query(
                "SELECT user_id FROM Likes WHERE film_id = ?",
                (rs, rn) -> rs.getLong("user_id"), film.getId()
        ));
        film.setLikes(likes);

        return film;
    }
}
