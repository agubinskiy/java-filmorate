package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/directors")
@Slf4j
public class DirectorController {

    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @PostMapping
    public DirectorDto create(@Valid @RequestBody NewDirectorRequest directorRequest) {
        return directorService.createDirector(directorRequest);
    }

    @GetMapping
    public List<DirectorDto> getDirectors() {
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public DirectorDto getDirectorById(@PathVariable("id") @Positive long id) {
        return directorService.getDirectorById(id);
    }

    @PutMapping
    public DirectorDto updateDirector(@Valid @RequestBody UpdateDirectorRequest request) {
        return directorService.updateDirector(request);
    }

    @DeleteMapping("/{id}")
    public void deleteLike(@PathVariable("id") @Positive long id) {
        directorService.deleteDirector(id);
    }

}
