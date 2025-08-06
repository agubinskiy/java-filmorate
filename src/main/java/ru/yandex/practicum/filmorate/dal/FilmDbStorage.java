package ru.yandex.practicum.filmorate.dal;

import jakarta.transaction.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.dto.SearchBy;
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

@Repository("filmDbStorage")
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM Films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM Films WHERE id = ?";
    private static final String FIND_MOST_LIKED_QUERY =
            "SELECT f.*, COUNT(l.user_id) AS likes_count FROM Films f " +
                    "LEFT JOIN Likes l ON f.id = l.film_id " +
                    "GROUP BY f.id ORDER BY likes_count DESC LIMIT ?";
    private static final String INSERT_FILM_QUERY = "INSERT INTO Films(name, description, release_date, duration, " +
            "rate_id) VALUES(?, ?, ?, ?, ?)";
    private static final String INSERT_GENRE_QUERY = "INSERT INTO FilmGenres(film_id, genre_id) VALUES(?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE Films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, rate_id = ? WHERE id = ?";
    private static final String DELETE_GENRE_QUERY = "DELETE FROM FilmGenres WHERE film_id = ?";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO Likes(film_id, user_id) VALUES (?, ?)";
    private static final String FIND_USERS_LIKES = "SELECT film_id FROM Likes WHERE user_id = ?";
    // private static final String FIND_COMMON_FILMS = "SELECT film_id FROM Likes WHERE user_id = ? INTERSECT " +
     //       "SELECT film_id FROM Likes WHERE user_id = ?";
    private static final String FIND_COMMON_FILMS = "SELECT f.* FROM films f JOIN ( SELECT film_id FROM Likes WHERE user_id = ? " +
            "INTERSECT SELECT film_id  FROM Likes WHERE user_id = ?) common_likes (film_id) ON f.id = common_likes.film_id;";
    private static final String DELETE_FILM_QUERY = "DELETE FROM Films WHERE id = ?";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM Likes WHERE user_id = ? AND film_id =?";
    private static final String FIND_MOST_LIKED_BY_GENRE_YEAR_QUERY =
            "SELECT f.*, count(l.user_id) as likes_count FROM Films f JOIN Likes l ON f.id = l.film_id " +
                    "WHERE exists (select 1 from FilmGenres where film_id = f.id and genre_id = ?)" +
                    "and EXTRACT(YEAR FROM f.release_date) = ?" +
                    "GROUP BY f.id ORDER BY likes_count DESC LIMIT ?";
    private static final String FIND_MOST_LIKED_BY_YEAR_QUERY =
            "SELECT f.*, count(l.user_id) as likes_count FROM Films f JOIN Likes l ON f.id = l.film_id " +
                    "WHERE EXTRACT(YEAR FROM f.release_date) = ?" +
                    "GROUP BY f.id ORDER BY likes_count DESC LIMIT ?";
    private static final String FIND_MOST_LIKED_BY_GENRE_QUERY =
            "SELECT f.*, count(l.user_id) as likes_count FROM Films f JOIN Likes l ON f.id = l.film_id " +
                    "WHERE exists (select 1 from FilmGenres where film_id = f.id and genre_id = ?)" +
                    "GROUP BY f.id ORDER BY likes_count DESC LIMIT ?";
    private static final String INSERT_DIRECTOR_QUERY = "INSERT INTO film_directors(film_id, director_id) VALUES(?, ?)";
    private static final String FIND_BY_ID_DIRECTOR_FILM =
            "SELECT f.* " +
                    "FROM Films f " +
                    "JOIN film_directors fd ON f.id = fd.film_id " +
                    "WHERE fd.director_id = ?";
    private static final String DELETE_FILM_DIRECTORS = "DELETE FROM film_directors WHERE film_id = ?";
    private static final String INSERT_FILM_DIRECTORS = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";

    public FilmDbStorage(JdbcTemplate jdbc) {
        super(jdbc);
    }

    public List<Film> getFilmsByIdDirector(long directorId) {
        Map<Long, List<Genre>> genres = findGenresForFilms();
        Map<Long, Set<Long>> likes = findLikesForFilms();
        Map<Long, List<Director>> directors = findDirectorsForFilms();
        RowMapper<Film> mapper = new FilmRowMapper(genres, likes, directors);
        List<Film> films = jdbc.query(FIND_BY_ID_DIRECTOR_FILM, mapper, directorId);
        return films;
    }

    // Метод для вывода режиссёров в фильмах
    private Map<Long, List<Director>> findDirectorsForFilms() {
        Map<Long, List<Director>> filmsDirectors = new HashMap<>();
        jdbc.query("SELECT fd.film_id, d.directors_id, d.name " +
                "FROM film_directors fd " +
                "JOIN directors d ON fd.director_id = d.directors_id", rs -> {
            Long filmId = rs.getLong("film_id");
            Director director = new Director();
            director.setId(rs.getLong("directors_id"));
            director.setName(rs.getString("name"));
            filmsDirectors.computeIfAbsent(filmId, k -> new ArrayList<>()).add(director);
        });
        return filmsDirectors;
    }

    public List<Film> findAllFilms() {
        Map<Long, List<Genre>> genres = findGenresForFilms();
        Map<Long, Set<Long>> likes = findLikesForFilms();
        Map<Long, List<Director>> directors = findDirectorsForFilms();
        RowMapper<Film> mapper = new FilmRowMapper(genres, likes, directors);
        return findMany(FIND_ALL_QUERY, mapper);
    }

    public Optional<Film> getFilm(Long id) {
        Map<Long, List<Genre>> genres = findGenresForFilms(Collections.singletonList(id));
        Map<Long, Set<Long>> likes = findLikesForFilms(Collections.singletonList(id));
        Map<Long, List<Director>> directors = findDirectorsForFilms();
        RowMapper<Film> mapper = new FilmRowMapper(genres, likes, directors);
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
        Map<Long, List<Director>> directors = findDirectorsForFilms();
        RowMapper<Film> mapper = new FilmRowMapper(genres, likes, directors);
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

    public void saveFilmDirectors(long filmId, List<Director> directors) {
        List<Object[]> batchArgs = new ArrayList<>();
        for (Director director : directors) {
            // Создаем массив объектов для текущего жанра
            Object[] args = new Object[2];
            // Первый элемент — id фильма
            args[0] = filmId;
            // Второй элемент — id режиссёра
            args[1] = director.getId();
            // Добавляем массив аргументов в список batchArgs
            batchArgs.add(args);
        }
        // Выполняем пакетную вставку
        jdbc.batchUpdate(INSERT_DIRECTOR_QUERY, batchArgs);
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

    // Обновление режиссёров
    public void updateFilmDirectors(Long filmId, List<Director> directors) {
        // Удаляем прошлые связи
        jdbc.update(DELETE_FILM_DIRECTORS, filmId);

        // Добавляем новые связи
        if (directors != null && !directors.isEmpty()) {
            for (Director director : directors) {
                jdbc.update(INSERT_FILM_DIRECTORS, filmId, director.getId());
            }
        }
    }

    public Film addLike(Long filmId, Long userId) {
        insert(INSERT_LIKE_QUERY, filmId, userId);
        return getFilm(filmId).orElseThrow();
    }

    @Transactional
    public void deleteFilm(Long filmId) {
        delete(
                DELETE_FILM_QUERY,
                filmId
        );
    }

    /**
     * Сохраняем лайки всех пользователей в формате <userId, <filmId, rate>>
     */
    public Map<Long, Map<Long, Double>> getAllLikes() {
        String query = "SELECT * FROM Likes";
        Map<Long, Map<Long, Double>> result = new HashMap<>();
        jdbc.query(query, rs -> {
            Long filmId = rs.getLong("film_id");
            Long userId = rs.getLong("user_id");
            //Пока используем только лайки, поэтому рейтинг проставляем 1.0
            result.computeIfAbsent(userId, k -> new HashMap<>()).put(filmId, 1.0);
        });
        return result;
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        Map<Long, List<Genre>> genres = findGenresForFilms();
        Map<Long, Set<Long>> likes = findLikesForFilms();
        Map<Long, List<Director>> directors = findDirectorsForFilms();
        RowMapper<Film> mapper = new FilmRowMapper(genres, likes,directors);
        return findMany(FIND_COMMON_FILMS, mapper, userId, friendId);
    }

    @Override
    public void removeLike(Long userId, Long filmId) {
        delete(
                DELETE_LIKE_QUERY,
                userId,
                filmId
        );
    }

    public List<Film> searchFilms(String query, SearchBy by) {
        Object[] params = {"%" + query.toLowerCase() + "%"};
        String searchQuery = null;
        if (by.equals(SearchBy.TITLE)) {
            searchQuery = "SELECT * FROM Films WHERE LOWER(name) LIKE ?";
        } else if (by.equals(SearchBy.DIRECTOR)) {
            searchQuery = "SELECT * FROM Films f " +
                    "JOIN film_directors fd ON f.id = fd.film_id " +
                    "JOIN directors d ON fd.director_id = d.directors_id " +
                    "WHERE LOWER(d.name) LIKE ?";
        } else if (by.equals(SearchBy.DIRECTOR_AND_TITLE) || by.equals(SearchBy.TITLE_AND_DIRECTOR)) {
            searchQuery = "SELECT * FROM Films f " +
                    "LEFT JOIN film_directors fd ON f.id = fd.film_id " +
                    "LEFT JOIN directors d ON fd.director_id = d.directors_id " +
                    "WHERE LOWER(d.name) LIKE ? OR LOWER(f.name) LIKE ?";
            params = new Object[]{"%" + query.toLowerCase() + "%", "%" + query.toLowerCase() + "%"};
        }
        List<Long> filmIds = jdbc.query(searchQuery,
                (rs, rn) -> rs.getLong("id"),
                params);
        if (filmIds.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<Genre>> genres = findGenresForFilms(filmIds);
        Map<Long, Set<Long>> likes = findLikesForFilms(filmIds);
        Map<Long, List<Director>> directors = findDirectorsForFilms();
        RowMapper<Film> mapper = new FilmRowMapper(genres, likes, directors);
        return findMany(searchQuery, mapper, params);
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
        Map<Long, List<Director>> directors = findDirectorsForFilms();
        RowMapper<Film> mapper = new FilmRowMapper(genres, likes, directors);
        return findMany(FIND_MOST_LIKED_BY_GENRE_YEAR_QUERY, mapper, genreId, year, count);
    }

    public List<Film> getMostLikedFilmsByGenre(int count, long genreId) {
        List<Long> filmIds = jdbc.query(FIND_MOST_LIKED_BY_GENRE_QUERY,
                (rs, rn) -> rs.getLong("id"), genreId, count);
        if (filmIds.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<Genre>> genres = findGenresForFilms(filmIds);
        Map<Long, Set<Long>> likes = findLikesForFilms(filmIds);
        Map<Long, List<Director>> directors = findDirectorsForFilms();
        RowMapper<Film> mapper = new FilmRowMapper(genres, likes, directors);
        return findMany(FIND_MOST_LIKED_BY_GENRE_QUERY, mapper, genreId, count);
    }

    public List<Film> getMostLikedFilmsByYear(int count, int year) {
        List<Long> filmIds = jdbc.query(FIND_MOST_LIKED_BY_YEAR_QUERY,
                (rs, rn) -> rs.getLong("id"), year, count);
        if (filmIds.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<Genre>> genres = findGenresForFilms(filmIds);
        Map<Long, Set<Long>> likes = findLikesForFilms(filmIds);
        Map<Long, List<Director>> directors = findDirectorsForFilms();
        RowMapper<Film> mapper = new FilmRowMapper(genres, likes, directors);
        return findMany(FIND_MOST_LIKED_BY_YEAR_QUERY, mapper, year, count);
    }
}
