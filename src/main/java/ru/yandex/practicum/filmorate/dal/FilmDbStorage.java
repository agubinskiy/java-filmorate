package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    private static final String FIND_MOST_LIKED_BY_GENRE_YEAR_QUERY =
            "SELECT f.*, count(l.user_id) as likes_count FROM Films f JOIN Likes l ON f.id = l.film_id " +
                    "WHERE exists (select 1 from FilmGenres where film_id = f.id and genre_id = ?)" +
                    "and EXTRACT(YEAR FROM f.release_date) = ?" +
                    "GROUP BY f.id ORDER BY likes_count DESC LIMIT ?";

    public FilmDbStorage(JdbcTemplate jdbc) {
        super(jdbc);
    }

    public List<Film> findAllFilms() {
        Map<Long, List<Genre>> genres = findGenresForFilms();
        Map<Long, Set<Long>> likes = findLikesForFilms();
        RowMapper<Film> mapper = new FilmRowMapper(genres, likes);
        return findMany(FIND_ALL_QUERY, mapper);
    }

    public Optional<Film> getFilm(Long id) {
        Map<Long, List<Genre>> genres = findGenresForFilms(Collections.singletonList(id));
        Map<Long, Set<Long>> likes = findLikesForFilms(Collections.singletonList(id));
        RowMapper<Film> mapper = new FilmRowMapper(genres, likes);
        return findOne(FIND_BY_ID_QUERY, mapper, id);
    }

    public List<Film> getMostLikedFilms(int count) {
        List<Long> filmIds = jdbc.query(FIND_MOST_LIKED_QUERY,
                (rs, rn) -> rs.getLong("id"), count);
        if (filmIds.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<Genre>> genres = findGenresForFilms(filmIds);
        Map<Long, Set<Long>> likes = findLikesForFilms(filmIds);
        RowMapper<Film> mapper = new FilmRowMapper(genres, likes);
        return findMany(FIND_MOST_LIKED_QUERY, mapper, count);

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
        return getFilm(filmId).orElseThrow();
    }

    private Map<Long, List<Genre>> findGenresForFilms() {
        String query = "SELECT * FROM FilmGenres";
        Map<Long, List<Genre>> result = new HashMap<>();
        jdbc.query(query, rs -> {
            Long filmId = rs.getLong("film_id");
            Genre genre = Genre.fromId(rs.getInt("genre_id"));
            result.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genre);
        });
        return result;
    }

    private Map<Long, Set<Long>> findLikesForFilms() {
        String query = "SELECT * FROM Likes";
        Map<Long, Set<Long>> result = new HashMap<>();
        jdbc.query(query, rs -> {
            Long filmId = rs.getLong("film_id");
            Long userId = rs.getLong("user_id");
            result.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        });
        return result;
    }

    private Map<Long, List<Genre>> findGenresForFilms(Collection<Long> filmIds) {
        String query = "SELECT * FROM FilmGenres WHERE film_id IN (" +
                filmIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";
        Map<Long, List<Genre>> result = new HashMap<>();
        jdbc.query(query, rs -> {
            Long filmId = rs.getLong("film_id");
            Genre genre = Genre.fromId(rs.getInt("genre_id"));
            result.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genre);
        });
        return result;
    }

    private Map<Long, Set<Long>> findLikesForFilms(Collection<Long> filmIds) {
        String query = "SELECT * FROM Likes WHERE film_id IN (" +
                filmIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";
        Map<Long, Set<Long>> result = new HashMap<>();
        jdbc.query(query, rs -> {
            Long filmId = rs.getLong("film_id");
            Long userId = rs.getLong("user_id");
            result.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        });
        return result;
    }

    public List<Film> getMostLikedFilmsByGenreYear(int count, long genreId, int year) {
        List<Long> filmIds = jdbc.query(FIND_MOST_LIKED_BY_GENRE_YEAR_QUERY,
                (rs, rn) -> rs.getLong("id"), genreId, year, count);
        if (filmIds.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<Genre>> genres = findGenresForFilms(filmIds);
        Map<Long, Set<Long>> likes = findLikesForFilms(filmIds);
        RowMapper<Film> mapper = new FilmRowMapper(genres, likes);
        return findMany(FIND_MOST_LIKED_BY_GENRE_YEAR_QUERY, mapper, genreId, year, count);

    }
}
