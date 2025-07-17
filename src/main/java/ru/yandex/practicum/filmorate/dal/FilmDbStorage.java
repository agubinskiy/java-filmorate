package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Optional;

@Repository("filmDboStorage")
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM Films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM Films WHERE id = ?";
    private static final String FIND_MOST_LIKED_QUERY =
            "SELECT f.*, count(l.user_id) as likes_count FROM Films f JOIN Likes l ON f.id = l.film_id " +
                    "GROUP BY f.id ORDER BY likes_count DESC LIMIT ?";
    private static final String INSERT_FILM_QUERY = "INSERT INTO Films(name, description, release_date, duration, " +
            "rate_id) VALUES(?, ?, ?, ?, ?)";
    private static final String INSERT_GENRE_QUERY = "INSERT INTO FilmGenres(film_id, genre_id) VALUES(?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE Films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, rate_id = ? WHERE id = ?";
    private static final String DELETE_GENRE_QUERY = "DELETE FROM FilmGenres WHERE film_id = ?";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO Likes(film_id, user_id) VALUES (?, ?)";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public List<Film> findAllFilms() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Film> getFilm(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<Film> getMostLikedFilms(int count) {
        return findMany(FIND_MOST_LIKED_QUERY, count);
    }

    public Film addFilm(Film film) {
        long id = insert(
                INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);

        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> insert(INSERT_GENRE_QUERY, id, genre.getId()));
        }

        return film;
    }

    public Film updateFilm(Film film) {
        update(
                UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        delete(DELETE_GENRE_QUERY, film.getId());
        film.getGenres().forEach(genre -> insert(INSERT_GENRE_QUERY, film.getId(), genre.getId()));
        return film;
    }

    public Film addLike(Long filmId, Long userId) {
        insert(INSERT_LIKE_QUERY, filmId, userId);
        return getFilm(filmId).get();
    }
}
