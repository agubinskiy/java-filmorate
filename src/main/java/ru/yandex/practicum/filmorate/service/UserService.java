package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.util.List;

import static ru.yandex.practicum.filmorate.mapper.UserMapper.mapToUser;
import static ru.yandex.practicum.filmorate.mapper.UserMapper.mapToUserDto;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    @Autowired
    public UserService(@Qualifier("userDboStorage") UserStorage userStorage, EventStorage eventStorage) {
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
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
        User user = mapToUser(request);
        log.debug("Запрос на добавление пользователя конвертирован в объект класса User {}", user);
        user = userStorage.addUser(user);
        log.debug("Добавлен пользователь {}", user);
        return mapToUserDto(user);
    }

    public UserDto updateUser(UpdateUserRequest request) {
        log.debug("Начинается обновление пользователя по запросу {}", request);
        User updatedUser = userStorage.getUser(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> {
                    log.warn("Ошибка при обновлении пользователя. Пользователь с id={} не найден", request.getId());
                    return new NotFoundException("Пользователь с id=" + request.getId() + " не найден");
                });
        log.debug("Запрос на обновление пользователя конвертирован в объект класса User {}", updatedUser);
        updatedUser = userStorage.updateUser(updatedUser);
        log.debug("Обновлён пользователь {}", updatedUser);
        return mapToUserDto(updatedUser);
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
        log.info("Друг {} успешно добавлен пользователю {}.", friendId, userId);
        // отправить событие в ленту
        eventStorage.addEvent(Event.builder()
                .userId(userId)
                .eventType(EventType.FRIEND)
                .operation(OperationType.ADD)
                .timestamp(Instant.now().toEpochMilli())
                .entityId(friendId)
                .build());
        return mapToUserDto(userStorage.addFriend(userId, friendId));
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
        log.info("Друг {} успешно удалён у пользователя {}.", friendId, userId);
        //отправить событие в ленту
        eventStorage.addEvent(Event.builder()
                .userId(userId)
                .eventType(EventType.FRIEND)
                .operation(OperationType.REMOVE)
                .timestamp(Instant.now().toEpochMilli())
                .entityId(friendId)
                .build());
        return mapToUserDto(userStorage.deleteFriend(userId, friendId));
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
        log.info("Список общих друзей пользователей {} и {} успешно получен", userId1, userId2);
        return userStorage.getCommonFriends(userId1, userId2).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public List<UserDto> getFriends(Long userId) {
        if (userStorage.getUser(userId).isEmpty()) {
            log.warn("Ошибка при запросе списка друзей. Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        log.info("Список друзей пользователя {} успешно получен", userId);
        return userStorage.getFriends(userId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public List<EventDTO> getFeed(Long userId) {
        if (userStorage.getUser(userId).isEmpty()) {
            log.warn("Ошибка при запросе ленты пользователя. Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        log.info("Лента пользователя {} успешно получена", userId);
        return eventStorage.getFeed(userId).stream()
                .map(EventMapper::mspToEventDto)
                .toList();

    }

    public void deleteUser(Long id) {
        if (userStorage.getUser(id).isEmpty()) {
            log.warn("Ошибка при запросе удаления пользователя. Пользователь с id={} не найден", id);
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        log.info("Пользователь {} успешно удалён.", id);
        userStorage.deleteUser(id);
    }
}
