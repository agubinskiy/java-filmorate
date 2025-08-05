package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorDbStorage;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DirectorService {

    private final DirectorDbStorage directorDbStorage;

    public DirectorService(@Qualifier("directorDbStorage") DirectorDbStorage directorDbStorage) {
        this.directorDbStorage = directorDbStorage;
    }

    public DirectorDto createDirector(NewDirectorRequest request) {
        log.debug("Начинается добавление режиссёра по запросу {}", request);
        Director director = DirectorMapper.mapToDirector(request);
        log.debug("Запрос на добавление режиссёра конвертирован в объект класса Director {}", director);

        director = directorDbStorage.save(director);
        log.debug("Добавление режиссёра успешно {}", director);
        return DirectorMapper.mapToDirectorDto(director);
    }


    public List<DirectorDto> getDirectors() {
        List<Director> directors = directorDbStorage.findAll();
        return directors.stream()
                .map(DirectorMapper::mapToDirectorDto)
                .collect(Collectors.toList());
    }

    public DirectorDto getDirectorById(long directorId) {

        Director director = directorDbStorage.findById(directorId)
                .orElseThrow(() -> new NotFoundException("Режиссер не найден с id: " + directorId));

        return DirectorMapper.mapToDirectorDto(director);
    }

    public DirectorDto updateDirector(UpdateDirectorRequest request) {
        log.debug("Начинается обновление режиссёра по запросу {}", request);
        Director updatedDirector = directorDbStorage.findById(request.getId())
                .map(user -> DirectorMapper.updateDirectorFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        updatedDirector = directorDbStorage.update(updatedDirector);
        log.debug("Обновление успешно {}", updatedDirector);
        return DirectorMapper.mapToDirectorDto(updatedDirector);
    }

    public void deleteDirector(long directorId) {
        log.debug("Начинается удаление режиссёра с id: {}", directorId);
        if (directorDbStorage.findById(directorId).isEmpty()) {
            log.warn("Ошибка при удалении режиссёра. Режиссёр с id={} не найден", directorId);
            throw new NotFoundException("Режиссёр с id=" + directorId + " не найден");
        }
        directorDbStorage.deleteFilmLike(directorId);
    }

}
