package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDboStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<UserDto> findAllUsers() {
        return userStorage.findAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto getUser(Long userId) {
        return userStorage.getUser(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> {
                    log.warn("Ошибка при поиске пользователя. Пользователь с id={} не найден", userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не найден");
                });
    }

    public UserDto addUser(NewUserRequest request) {
        log.debug("Начинается добавление пользователя по запросу {}", request);
        User user = UserMapper.mapToUser(request);
        user = userStorage.addUser(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(UpdateUserRequest request) {
        log.debug("Начинается обновление пользователя по запросу {}", request);
        User updatedUser = userStorage.getUser(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> {
                    log.warn("Ошибка при обновлении пользователя. Пользователь с id={} не найден", request.getId());
                    return new NotFoundException("Пользователь с id=" + request.getId() + " не найден");
                });
        updatedUser = userStorage.updateUser(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public UserDto addFriend(Long userId, Long friendId) {
        if (userStorage.getUser(userId).isEmpty()) {
            log.warn("Ошибка при добавлении друга. Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        if (userStorage.getUser(friendId).isEmpty()) {
            log.warn("Ошибка при добавлении друга. Пользователь с id={} не найден", friendId);
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден");
        }
        log.info("Друг успешно добавлен.");
        return UserMapper.mapToUserDto(userStorage.addFriend(userId, friendId));
    }

    public UserDto deleteFriend(Long userId, Long friendId) {
        if (userStorage.getUser(userId).isEmpty()) {
            log.warn("Ошибка при удалении друга. Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        if (userStorage.getUser(friendId).isEmpty()) {
            log.warn("Ошибка при удалении друга. Пользователь с id={} не найден", friendId);
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден");
        }
        log.info("Друг успешно удалён.");
        return UserMapper.mapToUserDto(userStorage.deleteFriend(userId, friendId));
    }

    public List<UserDto> getCommonFriends(Long userId1, Long userId2) {
        if (userStorage.getUser(userId1).isEmpty()) {
            log.warn("Ошибка при поиске друзей. Пользователь с id={} не найден", userId1);
            throw new NotFoundException("Пользователь с id=" + userId1 + " не найден");
        }
        if (userStorage.getUser(userId2).isEmpty()) {
            log.warn("Ошибка при поиске друзей. Пользователь с id={} не найден", userId2);
            throw new NotFoundException("Пользователь с id=" + userId2 + " не найден");
        }
        log.info("Список общих друзей успешно получен");
        return userStorage.getCommonFriends(userId1, userId2).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public List<UserDto> getFriends(Long userId) {
        if (userStorage.getUser(userId).isEmpty()) {
            log.warn("Ошибка при запросе списка друзей. Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        log.info("Список друзей успешно получен");
        return userStorage.getFriends(userId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }
}
