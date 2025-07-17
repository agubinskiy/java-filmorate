package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    @Getter
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private final Set<String> logins = new HashSet<>();
    private long counter;

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public Optional<User> getUser(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User addUser(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin()); //если имя не указано, в качестве имени используем логин
        }
        checkEmailDuplicate(user.getEmail());
        checkLoginDuplicate(user.getLogin());
        user.setId(++counter);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        logins.add(user.getLogin());
        log.info("Пользователь успешно добавлен {}", user);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        User oldUser = getUser(newUser.getId()).orElseThrow(() -> {
            log.warn("Ошибка при обновлении пользователя. Не найдено пользователя с идентификатором {}",
                    newUser.getId());
            return new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        });
        if (newUser.getEmail() != null) {
            if (!newUser.getEmail().equals(oldUser.getEmail())) {
                checkEmailDuplicate(newUser.getEmail());
            }
            oldUser.setEmail(newUser.getEmail());
            emails.add(newUser.getEmail());
        }
        if (newUser.getLogin() != null) {
            if (!newUser.getLogin().equals(oldUser.getLogin())) {
                checkLoginDuplicate(newUser.getLogin());
            }
            oldUser.setLogin(newUser.getLogin());
            logins.add(newUser.getLogin());
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

    @Override
    public List<User> getFriends(Long id) {
        return getListOfFriendsForId(getUser(id).get().getFriends());
    }

    @Override
    public List<User> getCommonFriends(Long user1Id, Long user2Id) {
        //копируем список друзей пользователя 1
        Set<Long> commonFriends = new HashSet<>(getUser(user1Id).get().getFriends());
        //оставляем только пересечения со списком друзей пользователя 2
        commonFriends.retainAll(getUser(user2Id).get().getFriends());
        return getListOfFriendsForId(commonFriends);
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        getUser(userId).get().getFriends().add(friendId);
        return getUser(userId).get();
    }

    @Override
    public User deleteFriend(Long userId, Long friendId) {
        getUser(userId).get().getFriends().remove(friendId);
        return getUser(userId).get();
    }

    //Проверка, что такой почты еще не зарегистрировано
    private void checkEmailDuplicate(String email) {
        if (emails.contains(email)) {
            log.warn("Ошибка при обновлении пользователя. Почта {} уже используется", email);
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
    }

    //Проверка, что такого логина еще не зарегистрировано
    private void checkLoginDuplicate(String login) {
        if (logins.contains(login)) {
            log.warn("Ошибка при обновлении пользователя. Логин {} уже используется", login);
            throw new DuplicatedDataException("Этот логин уже используется");
        }
    }

    private List<User> getListOfFriendsForId(Set<Long> list) {
        List<User> result = new ArrayList<>();
        for (Long id : list) {
            result.add(getUser(id).get());
        }
        return result;
    }
}
