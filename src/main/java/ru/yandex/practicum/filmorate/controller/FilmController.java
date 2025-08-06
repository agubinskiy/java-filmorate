package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.SearchBy;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.CreateValidation;
import ru.yandex.practicum.filmorate.model.UpdateValidation;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<FilmDto> findAllFilms() {
        log.info("Запрошен список всех фильмов");
        return filmService.findAllFilms();
    }

    @GetMapping("/{filmId}")
    public FilmDto getFilm(@PathVariable Long filmId) {
        log.info("Запрошена информация по фильму id={}", filmId);
        log.debug("Иниформация: {}", filmService.getFilm(filmId));
        return filmService.getFilm(filmId);
    }

    /*@GetMapping("/popular")
    public List<FilmDto> getMostLikedFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрошен список из {} самых популярных фильмов", count);
        return filmService.getMostLikedFilms(count);
    }*/

    @GetMapping("/search")
    public List<FilmDto> searchFilms(@RequestParam String query, @RequestParam String by) {
        log.info("Запрошен поиск фильмов");
        return filmService.searchFilms(query, SearchBy.fromString(by));
    }

    @PostMapping
    public FilmDto addFilm(@Validated(CreateValidation.class) @RequestBody NewFilmRequest request) {
        log.info("Добавляется фильм");
        return filmService.addFilm(request);
    }

    @PutMapping
    public FilmDto updateFilm(@Validated(UpdateValidation.class) @RequestBody UpdateFilmRequest request) {
        log.info("Обновляется фильм {}", request.getId());
        return filmService.updateFilm(request);
    }

    @PutMapping("/{id}/like/{userId}")
    public FilmDto addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Добавляется лайк фильму id={} от пользователя userId={}", id, userId);
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public FilmDto deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Удаляется лайк фильму id={} от пользователя userId={}", id, userId);
        return filmService.deleteLike(id, userId);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable Long filmId) {
        log.info("Удаляется фильм id={}", filmId);
        filmService.deleteFilm(filmId);
        log.info("Фильм с id={} удален", filmId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getMostLikedFilms(@RequestParam(defaultValue = "1000") int count,
                                           @RequestParam(defaultValue = "0") long genreId,
                                           @RequestParam(defaultValue = "0") int year
    ) {
        log.info("Запрошен список из {} самых популярных фильмов по жанру {} и году {}", count, genreId, year);
        return filmService.getMostLikedFilmsByGenreYear(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDto> getDirectFilms(
            @PathVariable("directorId") @Positive long directorId,
            @RequestParam(value = "sortBy", required = false) String sortBy) {
        log.info("Запрошен список фильмов по режиссёру с сортировкой: {}", sortBy);

        if ("likes".equals(sortBy)) {
            return filmService.getFilmsDirectorSortByLikes(directorId);
        } else if ("year".equals(sortBy)) {
            return filmService.getFilmsDirectorSortByYear(directorId);
        } else {
            return filmService.getDirectorFilms(directorId);
        }
    }

    @GetMapping("/common")
    public List<FilmDto> getCommonFilms(@RequestParam long userId, @RequestParam long friendId) {
        log.info("Запрошен список общих фильмо в пользователей с userId {} и friendId = {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

}
