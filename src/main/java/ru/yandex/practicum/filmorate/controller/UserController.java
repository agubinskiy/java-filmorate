package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
@Getter
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        for (User userFromMap : users.values()) {
            if (userFromMap.getEmail().equals(user.getEmail())) {
                log.warn("Ошибка при добавлении пользователя. Почта {} уже используется",
                        user.getEmail());
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            if (userFromMap.getLogin().equals(user.getLogin())) {
                log.warn("Ошибка при добавлении пользователя. Логин {} уже используется",
                        user.getLogin());
                throw new DuplicatedDataException("Этот логин уже используется");
            }
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.warn("Ошибка при обновлении пользователя. Не передан Id пользователя");
            throw new ValidationException("Id пользователя не может быть пустым");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getEmail() != null) {
                if (!newUser.getEmail().equals(oldUser.getEmail())) {
                    for (User userFromMap : users.values()) {
                        if (userFromMap.getEmail().equals(newUser.getEmail())) {
                            log.warn("Ошибка при обновлении пользователя. Почта {} уже используется",
                                    newUser.getEmail());
                            throw new DuplicatedDataException("Этот имейл уже используется");
                        }
                    }
                }
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getLogin() != null) {
                if (!newUser.getLogin().equals(oldUser.getLogin())) {
                    for (User userFromMap : users.values()) {
                        if (userFromMap.getLogin().equals(newUser.getLogin())) {
                            log.warn("Ошибка при обновлении пользователя. Логин {} уже используется",
                                    newUser.getLogin());
                            throw new DuplicatedDataException("Этот логин уже используется");
                        }
                    }
                }
                oldUser.setLogin(newUser.getLogin());
            }
            if (newUser.getBirthday() != null) {
                oldUser.setBirthday(newUser.getBirthday());
            }
            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
            }
            log.info("Пользователь успешно обновлён {}", oldUser);
            return oldUser;
        }
        log.warn("Ошибка при обновлении пользователя. Не найдено пользователя с идентификатором {}",
                newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
