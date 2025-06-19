package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private FilmService filmService;
    private FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmService);
    }

    @Test
    public void addFilm() {
        Film film = Film.builder()
                .id(1L)
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 5, 10))
                .duration(120)
                .build();
        filmController.addFilm(film);

        assertNotNull(filmStorage.getFilms());
        assertEquals(1, filmStorage.getFilms().size());
        assertEquals("Name", filmStorage.getFilms().get(1L).getName(),
                "Название фильма некорректно");
    }

    @Test
    public void updateFilm() {
        Film film1 = Film.builder()
                .id(1L)
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 5, 10))
                .duration(120)
                .build();
        Film film2 = Film.builder()
                .id(1L)
                .name("Name2")
                .description("Description2")
                .releaseDate(LocalDate.of(2001, 6, 11))
                .duration(130)
                .build();
        filmController.addFilm(film1);
        filmController.updateFilm(film2);

        assertNotNull(filmStorage.getFilms());
        assertEquals(1, filmStorage.getFilms().size());
        assertEquals("Name2", filmStorage.getFilms().get(1L).getName(),
                "Название фильма некорректно");
    }

    @Test
    public void getAllFilms() {
        Film film1 = Film.builder()
                .id(1L)
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 5, 10))
                .duration(120)
                .build();
        Film film2 = Film.builder()
                .id(2L)
                .name("Name2")
                .description("Description2")
                .releaseDate(LocalDate.of(2001, 6, 11))
                .duration(130)
                .build();
        filmController.addFilm(film1);
        filmController.addFilm(film2);
        Collection<Film> result = filmController.findAllFilms();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(filmStorage.getFilms().values(), result,
                "Список фильмов некорректен");
    }

    @Test
    public void addFilmWith200symbolsDescription() {
        Film film = Film.builder()
                .id(1L)
                .name("Name")
                .description("Тестовое описание фильма, проверка передачи в поле с описанием более 200 символов. Тест" +
                        "ТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТест" +
                        "ТестТестТестТестТест1")
                .releaseDate(LocalDate.of(2000, 5, 10))
                .duration(120)
                .build();
        filmController.addFilm(film);

        assertNotNull(filmStorage.getFilms());
        assertEquals(1, filmStorage.getFilms().size());
        assertEquals("Name", filmStorage.getFilms().get(1L).getName(),
                "Название фильма некорректно");
    }

    @Test
    public void addFilmWithReleaseDate18951228() {
        Film film = Film.builder()
                .id(1L)
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(120)
                .build();
        filmController.addFilm(film);

        assertNotNull(filmStorage.getFilms());
        assertEquals(1, filmStorage.getFilms().size());
        assertEquals("Name", filmStorage.getFilms().get(1L).getName(),
                "Название фильма некорректно");
    }

    @Test
    public void addFilmWithReleaseDateBefore18951228() {
        Film film = Film.builder()
                .id(1L)
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(120)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        assertEquals("releaseDate", exception.getParameter());
        assertEquals("Дата релиза не может быть до 28.12.1895", exception.getReason());
    }

    @Test
    public void addFilmWithDuration1() {
        Film film = Film.builder()
                .id(1L)
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 5, 10))
                .duration(1)
                .build();
        filmController.addFilm(film);

        assertNotNull(filmStorage.getFilms());
        assertEquals(1, filmStorage.getFilms().size());
        assertEquals("Name", filmStorage.getFilms().get(1L).getName(),
                "Название фильма некорректно");
    }

    @Test
    public void updateFilmWithReleaseDateBefore18951228() {
        Film film1 = Film.builder()
                .id(1L)
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 5, 10))
                .duration(120)
                .build();
        Film film2 = Film.builder()
                .id(1L)
                .name("Name2")
                .description("Description2")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(130)
                .build();
        filmController.addFilm(film1);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.updateFilm(film2));
        assertEquals("releaseDate", exception.getParameter());
        assertEquals("Дата релиза не может быть до 28.12.1895", exception.getReason());
    }

    @Test
    public void updateFilmNotFoundId() {
        Film film1 = Film.builder()
                .id(1L)
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 5, 10))
                .duration(120)
                .build();
        Film film2 = Film.builder()
                .id(2L)
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 5, 10))
                .duration(0)
                .build();
        filmController.addFilm(film1);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.updateFilm(film2));
        assertEquals("Фильм с id = 2 не найден", exception.getMessage());
    }
}
