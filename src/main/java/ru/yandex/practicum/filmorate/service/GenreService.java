package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@Slf4j
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<GenreDto> findAllGenres() {
        return genreStorage.findAllGenres()
                .stream()
                .map(GenreMapper::mapToGenreDto)
                .toList();
    }

    public GenreDto getGenre(Integer id) {
        return genreStorage.getGenre(id)
                .map(GenreMapper::mapToGenreDto)
                .orElseThrow(() -> {
                    log.warn("Ошибка при поиске жанра. Жанр с id={} не найден", id);
                    return new NotFoundException("Жанр с идентификатором id=" + id + " не найден");
                });
    }
}
