//package ru.yandex.practicum.filmorate;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.model.Genre;
//import ru.yandex.practicum.filmorate.model.Rate;
//import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
//
//import java.time.LocalDate;
//import java.util.Collection;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//public class InMemoryFilmStorageTest {
//    private InMemoryFilmStorage filmStorage;
//
//    @BeforeEach
//    public void beforeEach() {
//        filmStorage = new InMemoryFilmStorage();
//    }
//
//    @Test
//    public void addFilm() {
//        Film film = Film.builder()
//                .id(1L)
//                .name("Name")
//                .description("Description")
//                .releaseDate(LocalDate.of(2000, 5, 10))
//                .duration(120)
//                .mpa(Rate.PG)
//                .genres(List.of(Genre.ACTION))
//                .build();
//        filmStorage.addFilm(film);
//
//        assertNotNull(filmStorage.getFilms());
//        assertEquals(1, filmStorage.getFilms().size());
//        assertEquals("Name", filmStorage.getFilms().get(1L).getName(),
//                "Название фильма некорректно");
//    }
//
//    @Test
//    public void updateFilm() {
//        Film film1 = Film.builder()
//                .id(1L)
//                .name("Name")
//                .description("Description")
//                .releaseDate(LocalDate.of(2000, 5, 10))
//                .duration(120)
//                .mpa(Rate.PG)
//                .genres(List.of(Genre.ACTION))
//                .build();
//        Film film2 = Film.builder()
//                .id(1L)
//                .name("Name2")
//                .description("Description2")
//                .releaseDate(LocalDate.of(2001, 6, 11))
//                .duration(130)
//                .mpa(Rate.PG)
//                .genres(List.of(Genre.ACTION))
//                .build();
//        filmStorage.addFilm(film1);
//        filmStorage.updateFilm(film2);
//
//        assertNotNull(filmStorage.getFilms());
//        assertEquals(1, filmStorage.getFilms().size());
//        assertEquals("Name2", filmStorage.getFilms().get(1L).getName(),
//                "Название фильма некорректно");
//    }
//
//    @Test
//    public void getAllFilms() {
//        Film film1 = Film.builder()
//                .id(1L)
//                .name("Name")
//                .description("Description")
//                .releaseDate(LocalDate.of(2000, 5, 10))
//                .duration(120)
//                .mpa(Rate.PG)
//                .genres(List.of(Genre.ACTION))
//                .build();
//        Film film2 = Film.builder()
//                .id(2L)
//                .name("Name2")
//                .description("Description2")
//                .releaseDate(LocalDate.of(2001, 6, 11))
//                .duration(130)
//                .mpa(Rate.PG)
//                .genres(List.of(Genre.ACTION))
//                .build();
//        filmStorage.addFilm(film1);
//        filmStorage.addFilm(film2);
//        Collection<Film> result = filmStorage.findAllFilms();
//
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertEquals(filmStorage.getFilms().values(), result,
//                "Список фильмов некорректен");
//    }
//
//    @Test
//    public void addFilmWith200symbolsDescription() {
//        Film film = Film.builder()
//                .id(1L)
//                .name("Name")
//                .description("Тестовое описание фильма, проверка передачи в поле с описанием более 200 символов. Тест" +
//                        "ТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТест" +
//                        "ТестТестТестТестТест1")
//                .releaseDate(LocalDate.of(2000, 5, 10))
//                .duration(120)
//                .mpa(Rate.PG)
//                .genres(List.of(Genre.ACTION))
//                .build();
//        filmStorage.addFilm(film);
//
//        assertNotNull(filmStorage.getFilms());
//        assertEquals(1, filmStorage.getFilms().size());
//        assertEquals("Name", filmStorage.getFilms().get(1L).getName(),
//                "Название фильма некорректно");
//    }
//
//    @Test
//    public void addFilmWithReleaseDate18951228() {
//        Film film = Film.builder()
//                .id(1L)
//                .name("Name")
//                .description("Description")
//                .releaseDate(LocalDate.of(1895, 12, 28))
//                .duration(120)
//                .mpa(Rate.PG)
//                .genres(List.of(Genre.ACTION))
//                .build();
//        filmStorage.addFilm(film);
//
//        assertNotNull(filmStorage.getFilms());
//        assertEquals(1, filmStorage.getFilms().size());
//        assertEquals("Name", filmStorage.getFilms().get(1L).getName(),
//                "Название фильма некорректно");
//    }
//
//    @Test
//    public void addFilmWithDuration1() {
//        Film film = Film.builder()
//                .id(1L)
//                .name("Name")
//                .description("Description")
//                .releaseDate(LocalDate.of(2000, 5, 10))
//                .duration(1)
//                .mpa(Rate.PG)
//                .genres(List.of(Genre.ACTION))
//                .build();
//        filmStorage.addFilm(film);
//
//        assertNotNull(filmStorage.getFilms());
//        assertEquals(1, filmStorage.getFilms().size());
//        assertEquals("Name", filmStorage.getFilms().get(1L).getName(),
//                "Название фильма некорректно");
//    }
//}
