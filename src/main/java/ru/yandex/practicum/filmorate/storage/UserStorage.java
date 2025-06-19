package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface UserStorage {
    public Collection<User> findAllUsers();

    public Optional<User> getUser(Long id);

    public User addUser(User user);

    public User updateUser(User user);

    public void deleteUser(Long id);

    public Map<Long, User> getUsers();
}
