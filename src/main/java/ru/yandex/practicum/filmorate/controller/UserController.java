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
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.EventDTO;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.CreateValidation;
import ru.yandex.practicum.filmorate.model.UpdateValidation;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        log.info("Запрошен список всех пользователей");
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        log.info("Запрошена информация по пользователю id={}", id);
        return userService.getUser(id);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(@PathVariable Long id) {
        log.info("Запрошен список друзей пользователя id={}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriend(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Запрошен список общих друзей пользователей id={} и {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping
    public UserDto addUser(@Validated(CreateValidation.class) @RequestBody NewUserRequest request) {
        log.info("Добавляется пользователь");
        return userService.addUser(request);
    }

    @PutMapping
    public UserDto updateUser(@Validated(UpdateValidation.class) @RequestBody UpdateUserRequest request) {
        log.info("Обновляется пользователь {}", request.getId());
        return userService.updateUser(request);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public UserDto addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Добавляется друг id={} пользователю userId={}", friendId, id);
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public UserDto deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Удаляется друг id={}  у пользователя userId={}", friendId, id);
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/feed")
    public List<EventDTO> getFeed(@PathVariable Long id) {
        log.info("Запрошена лента событий по пользователю с id={}", id);
        return userService.getFeed(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Удаляется пользователь id={} ", id);
        userService.deleteUser(id);
        log.info("Пользователь с  id={} удален", id);
    }
}
