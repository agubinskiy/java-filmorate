//package ru.yandex.practicum.filmorate;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.jdbc.core.JdbcTemplate;
//import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
//import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.model.Genre;
//import ru.yandex.practicum.filmorate.model.Rate;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@JdbcTest
//@AutoConfigureTestDatabase
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@Import({FilmDbStorage.class, FilmRowMapper.class})
//public class FilmDbStorageTest {
//    private final FilmDbStorage filmStorage;
//
//    private final JdbcTemplate jdbcTemplate;
//
//    @BeforeAll
//    static void setUp(@Autowired JdbcTemplate jdbcTemplate) {
//        jdbcTemplate.update("INSERT INTO Users(email, login, name, birthday) " +
//                "VALUES ('user1@mail.ru', 'login1', 'name1', '1990-01-01')");
//        jdbcTemplate.update("INSERT INTO Users(email, login, name, birthday) " +
//                "VALUES ('user2@mail.ru', 'login2', 'name2', '1995-02-02')");
//        jdbcTemplate.update("INSERT INTO Users(email, login, name, birthday) " +
//                "VALUES ('user3@mail.ru', 'login3', 'name3', '1996-03-03')");
//        jdbcTemplate.update("INSERT INTO Films(name, description, release_date, duration, " +
//                "rate_id) VALUES('film1', 'description1', '2000-01-01', 120, 1)");
//        jdbcTemplate.update("INSERT INTO Films(name, description, release_date, duration, " +
//                "rate_id) VALUES('film2', 'description2', '2002-02-02', 130, 2)");
//        jdbcTemplate.update("INSERT INTO Films(name, description, release_date, duration, " +
//                "rate_id) VALUES('film3', 'description3', '2003-03-03', 140, 3)");
//        jdbcTemplate.update("INSERT INTO FilmGenres(film_id, genre_id) VALUES(1, 1)");
//        jdbcTemplate.update("INSERT INTO FilmGenres(film_id, genre_id) VALUES(2, 2)");
//        jdbcTemplate.update("INSERT INTO FilmGenres(film_id, genre_id) VALUES(3, 3)");
//        jdbcTemplate.update("INSERT INTO Likes(film_id, user_id) VALUES (1, 1)");
//        jdbcTemplate.update("INSERT INTO Likes(film_id, user_id) VALUES (1, 2)");
//        jdbcTemplate.update("INSERT INTO Likes(film_id, user_id) VALUES (1, 3)");
//        jdbcTemplate.update("INSERT INTO Likes(film_id, user_id) VALUES (2, 2)");
//    }
//
//    @Test
//    public void testFindAllFilms() {
//
//        List<Film> films = filmStorage.findAllFilms();
//
//        assertThat(films).isNotEmpty();
//        assertThat(films).size().isEqualTo(3);
//    }
//
//    @Test
//    public void testFindUserById() {
//
//        Optional<Film> filmOptional = filmStorage.getFilm(1L);
//
//        assertThat(filmOptional)
//                .isPresent()
//                .hasValueSatisfying(film ->
//                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
//                )
//                .hasValueSatisfying(film ->
//                        assertThat(film).hasFieldOrPropertyWithValue("name", "film1")
//                );
//    }
//
//    @Test
//    public void testAddFilm() {
//        Film film = Film.builder()
//                .id(null)
//                .name("Name4")
//                .description("Description4")
//                .releaseDate(LocalDate.of(1900, 12, 28))
//                .duration(120)
//                .mpa(Rate.PG)
//                .genres(List.of(Genre.ACTION))
//                .build();
//        Film result = filmStorage.addFilm(film);
//
//        assertThat(result.getId()).isEqualTo(4);
//        assertThat(result.getName()).isEqualTo("Name4");
//    }
//
//    @Test
//    public void testUpdateFilm() {
//
//        Film film = Film.builder()
//                .id(2L)
//                .name("Name5")
//                .description("Description5")
//                .releaseDate(LocalDate.of(1900, 12, 28))
//                .duration(100)
//                .mpa(Rate.PG)
//                .genres(List.of(Genre.ACTION))
//                .build();
//        Film result = filmStorage.updateFilm(film);
//
//        assertThat(result.getId()).isEqualTo(2);
//        assertThat(result.getName()).isEqualTo("Name5");
//        assertThat(result.getDescription()).isEqualTo("Description5");
//        assertThat(result.getReleaseDate()).isEqualTo(LocalDate.of(1900, 12, 28));
//        assertThat(result.getDuration()).isEqualTo(100);
//        assertThat(result.getMpa()).isEqualTo(Rate.PG);
//    }
//
//    @Test
//    public void testAddLike() {
//
//        Film result = filmStorage.addLike(2L, 1L);
//
//        assertThat(result.getLikes()).isNotEmpty();
//        assertThat(result.getLikes()).size().isEqualTo(2);
//    }
//
//    @Test
//    public void testMostLiked() {
//        List<Film> result = filmStorage.getMostLikedFilms(2);
//
//        assertThat(result).isNotEmpty();
//        assertThat(result).size().isEqualTo(2);
//        assertThat(result.get(0).getId()).isEqualTo(1);
//        assertThat(result.get(1).getId()).isEqualTo(2);
//    }
//}
