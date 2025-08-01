package ru.yandex.practicum.filmorate.controller;

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
        return filmService.getFilm(filmId);
    }
    /*
    @GetMapping("/popular")
    public List<FilmDto> getMostLikedFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрошен список из {} самых популярных фильмов", count);
        return filmService.getMostLikedFilms(count);
    }
    */
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

    @GetMapping("/popular")
    public List<FilmDto> getMostLikedFilms(@RequestParam(defaultValue = "1000") int count,
                                           @RequestParam(defaultValue = "0") long genreId,
                                           @RequestParam(defaultValue = "0") int year
    ) {
        log.info("Запрошен список из {} самых популярных фильмов по жанру {} и году {}", count, genreId, year);
        return filmService.getMostLikedFilmsByGenreYear(count, genreId, year);
    }
}
