package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAllUsers();

    Optional<User> getUser(Long id);

    User addUser(User user);

    User updateUser(User user);

    Map<Long, User> getUsers();
}
