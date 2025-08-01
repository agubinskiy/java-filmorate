package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAllUsers();

    Optional<User> getUser(Long id);

    User addUser(User user);

    User updateUser(User user);

    List<User> getFriends(Long id);

    List<User> getCommonFriends(Long user1Id, Long user2Id);

    User addFriend(Long userId, Long friendId);

    User deleteFriend(Long userId, Long friendId);

  //  List<Event> getFeed(Long userId);
}
