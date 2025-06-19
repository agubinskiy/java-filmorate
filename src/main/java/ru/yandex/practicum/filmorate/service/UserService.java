package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User getUser(Long userId) {
        if (userStorage.getUser(userId).isEmpty()) {
            log.warn("Ошибка при поиске пользователя. Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        return userStorage.getUser(userId).get();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User newUser) {
        return userStorage.updateUser(newUser);
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    public User addFriend(Long userId, Long friendId) {
        if (userStorage.getUser(userId).isEmpty()) {
            log.warn("Ошибка при добавлении друга. Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        if (userStorage.getUser(friendId).isEmpty()) {
            log.warn("Ошибка при добавлении друга. Пользователь с id={} не найден", friendId);
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден");
        }
        userStorage.getUser(userId).get().getFriends().add(friendId); //добавляем друга в список друзей пользователя
        userStorage.getUser(friendId).get().getFriends().add(userId); //добавляем пользователя в список друзей друга
        log.info("Друг успешно добавлен.");
        return userStorage.getUser(friendId).get();
    }

    public User deleteFriend(Long userId, Long friendId) {
        if (userStorage.getUser(userId).isEmpty()) {
            log.warn("Ошибка при удалении друга. Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        if (userStorage.getUser(friendId).isEmpty()) {
            log.warn("Ошибка при удалении друга. Пользователь с id={} не найден", friendId);
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден");
        }
        userStorage.getUser(userId).get().getFriends().remove(friendId); //удаляем друга из списка друзей пользователя
        userStorage.getUser(friendId).get().getFriends().remove(userId); //удаляем пользователя из списка друзей друга
        log.info("Друг успешно удалён.");
        return userStorage.getUser(userId).get();
    }

    public List<User> getCommonFriends(Long userId1, Long userId2) {
        if (userStorage.getUser(userId1).isEmpty()) {
            log.warn("Ошибка при поиске друзей. Пользователь с id={} не найден", userId1);
            throw new NotFoundException("Пользователь с id=" + userId1 + " не найден");
        }
        if (userStorage.getUser(userId2).isEmpty()) {
            log.warn("Ошибка при поиске друзей. Пользователь с id={} не найден", userId2);
            throw new NotFoundException("Пользователь с id=" + userId2 + " не найден");
        }
        //копируем список друзей пользователя 1
        Set<Long> commonFriends = new HashSet<>(userStorage.getUser(userId1).get().getFriends());
        //оставляем только пересечения со списком друзей пользователя 2
        commonFriends.retainAll(userStorage.getUser(userId2).get().getFriends());
        log.info("Список общих друзей успешно получен");
        return getListOfFriendsForId(commonFriends);
    }

    public List<User> getFriends(Long userId) {
        if (userStorage.getUser(userId).isEmpty()) {
            log.warn("Ошибка при запросе списка друзей. Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        log.info("Список друзей успешно получен");
        return getListOfFriendsForId(userStorage.getUser(userId).get().getFriends());
    }

    private List<User> getListOfFriendsForId(Set<Long> list) {
        List<User> result = new ArrayList<>();
        for (Long id : list) {
            result.add(getUser(id));
        }
        return result;
    }
}
