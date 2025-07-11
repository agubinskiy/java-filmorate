package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
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
    public Map<Long, User> getUsers() {
        return users;
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
}
