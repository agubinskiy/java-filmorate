package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    public void addFilm() {
        Film film = new Film("Name", "Description",
                LocalDate.of(2000, 5, 10), 120);
        filmController.addFilm(film);

        assertNotNull(filmController.getFilms());
        assertEquals(1, filmController.getFilms().size());
        assertEquals("Name", filmController.getFilms().get(1).getName(),
                "Название фильма некорректно");
    }

    @Test
    public void updateFilm() {
        Film film1 = new Film("Name", "Description",
                LocalDate.of(2000, 5, 10), 120);
        Film film2 = new Film(1, "Name2", "Description2",
                LocalDate.of(2001, 6, 11), 130);
        filmController.addFilm(film1);
        filmController.updateFilm(film2);

        assertNotNull(filmController.getFilms());
        assertEquals(1, filmController.getFilms().size());
        assertEquals("Name2", filmController.getFilms().get(1).getName(),
                "Название фильма некорректно");
    }

    @Test
    public void getAllFilms() {
        Film film1 = new Film("Name", "Description",
                LocalDate.of(2000, 5, 10), 120);
        Film film2 = new Film("Name2", "Description2",
                LocalDate.of(2001, 6, 11), 130);
        filmController.addFilm(film1);
        filmController.addFilm(film2);
        Collection<Film> result = filmController.findAllFilms();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(filmController.getFilms().values(), result,
                "Список фильмов некорректен");
    }

    @Test
    public void addFilmWithEmptyName() {
        Film film = new Film("", "Description",
                LocalDate.of(2000, 5, 10), 120);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    public void addFilmWithTooLongDescription() {
        Film film = new Film("Name",
                "Тестовое описание фильма, проверка передачи в поле с описанием более 200 символов. Тест" +
                        "ТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТест" +
                        "ТестТестТестТестТест11",
                LocalDate.of(2000, 5, 10), 120);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        assertEquals("Описание фильма не может быть больше 200 символов", exception.getMessage());
    }

    @Test
    public void addFilmWith200symbolsDescription() {
        Film film = new Film("Name",
                "Тестовое описание фильма, проверка передачи в поле с описанием ровно 200 символов. Тест" +
                        "ТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТест" +
                        "ТестТестТестТестТест1",
                LocalDate.of(2000, 5, 10), 120);
        filmController.addFilm(film);

        assertNotNull(filmController.getFilms());
        assertEquals(1, filmController.getFilms().size());
        assertEquals("Name", filmController.getFilms().get(1).getName(),
                "Название фильма некорректно");
    }

    @Test
    public void addFilmWithReleaseDate18951228() {
        Film film = new Film("Name", "Description",
                LocalDate.of(1895, 12, 28), 120);
        filmController.addFilm(film);

        assertNotNull(filmController.getFilms());
        assertEquals(1, filmController.getFilms().size());
        assertEquals("Name", filmController.getFilms().get(1).getName(),
                "Название фильма некорректно");
    }

    @Test
    public void addFilmWithReleaseDateBefore18951228() {
        Film film = new Film("Name", "Description",
                LocalDate.of(1895, 12, 27), 120);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        assertEquals("Дата релиза некорректна", exception.getMessage());
    }

    @Test
    public void addFilmWithDuration1() {
        Film film = new Film("Name", "Description",
                LocalDate.of(2000, 5, 10), 1);
        filmController.addFilm(film);

        assertNotNull(filmController.getFilms());
        assertEquals(1, filmController.getFilms().size());
        assertEquals("Name", filmController.getFilms().get(1).getName(),
                "Название фильма некорректно");
    }

    @Test
    public void addFilmWithDuration0() {
        Film film = new Film("Name", "Description",
                LocalDate.of(2000, 5, 10), 0);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        assertEquals("Продолжительность фильма некорректна", exception.getMessage());
    }

    @Test
    public void addFilmWithDurationLessThan0() {
        Film film = new Film("Name", "Description",
                LocalDate.of(2000, 5, 10), -10);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        assertEquals("Продолжительность фильма некорректна", exception.getMessage());
    }

    @Test
    public void updateFilmWithEmptyName() {
        Film film1 = new Film("Name", "Description",
                LocalDate.of(2000, 5, 10), 120);
        Film film2 = new Film(1, "", "Description2",
                LocalDate.of(2001, 6, 11), 130);
        filmController.addFilm(film1);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.updateFilm(film2));
        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    public void updateFilmWithTooLongDescription() {
        Film film1 = new Film("Name", "Description",
                LocalDate.of(2000, 5, 10), 120);
        Film film2 = new Film(1, "Name2",
                "Тестовое описание фильма, проверка передачи в поле с описанием ровно 200 символов. Тест" +
                        "ТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТест" +
                        "ТестТестТестТестТест11",
                LocalDate.of(2001, 6, 11), 130);
        filmController.addFilm(film1);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.updateFilm(film2));
        assertEquals("Описание фильма не может быть больше 200 символов", exception.getMessage());
    }

    @Test
    public void updateFilmWithReleaseDateBefore18951228() {
        Film film1 = new Film("Name", "Description",
                LocalDate.of(2000, 5, 10), 120);
        Film film2 = new Film(1, "Name2", "Description2",
                LocalDate.of(1895, 12, 27), 130);
        filmController.addFilm(film1);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.updateFilm(film2));
        assertEquals("Дата релиза некорректна", exception.getMessage());
    }

    @Test
    public void updateFilmWithDuration0() {
        Film film1 = new Film("Name", "Description",
                LocalDate.of(2000, 5, 10), 120);
        Film film2 = new Film(1, "Name2", "Description2",
                LocalDate.of(2000, 5, 10), 0);
        filmController.addFilm(film1);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.updateFilm(film2));
        assertEquals("Продолжительность фильма некорректна", exception.getMessage());
    }

    @Test
    public void updateFilmNotFoundId() {
        Film film1 = new Film("Name", "Description",
                LocalDate.of(2000, 5, 10), 120);
        Film film2 = new Film(2, "Name2", "Description2",
                LocalDate.of(2000, 5, 10), 0);
        filmController.addFilm(film1);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.updateFilm(film2));
        assertEquals("Фильм с id = 2 не найден", exception.getMessage());
    }

    @Test
    public void updateFilmNoId() {
        Film film1 = new Film("Name", "Description",
                LocalDate.of(2000, 5, 10), 120);
        Film film2 = new Film("Name2", "Description2",
                LocalDate.of(2000, 5, 10), 0);
        filmController.addFilm(film1);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.updateFilm(film2));
        assertEquals("Id фильма не может быть пустым", exception.getMessage());
    }
}
