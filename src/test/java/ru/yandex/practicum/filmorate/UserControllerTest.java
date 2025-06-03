package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
    }

    @Test
    public void addUser() {
        User user = new User("mail@mail.ru", "user",
                "Name", LocalDate.of(2000, 1, 1));
        userController.addUser(user);

        assertNotNull(userController.getUsers());
        assertEquals(1, userController.getUsers().size());
        assertEquals("user", userController.getUsers().get(1).getLogin(),
                "Логин пользователя некорректен");
    }

    @Test
    public void updateUser() {
        User user1 = new User("mail@mail.ru", "user",
                "Name", LocalDate.of(2000, 1, 1));
        User user2 = new User(1, "mail2@mail.ru", "user2",
                "Name2", LocalDate.of(2000, 1, 2));
        userController.addUser(user1);
        userController.updateUser(user2);

        assertNotNull(userController.getUsers());
        assertEquals(1, userController.getUsers().size());
        assertEquals("user2", userController.getUsers().get(1).getLogin(),
                "Логин пользователя некорректен");
    }

    @Test
    public void getAllUsers() {
        User user1 = new User("mail@mail.ru", "user",
                "Name", LocalDate.of(2000, 1, 1));
        User user2 = new User("mail2@mail.ru", "user2",
                "Name2", LocalDate.of(2000, 1, 2));
        userController.addUser(user1);
        userController.addUser(user2);
        Collection<User> result = userController.findAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userController.getUsers().values(), result,
                "Список пользователей некорректен");
    }

    @Test
    public void addUserWithEmptyEmail() {
        User user = new User("", "user",
                "Name", LocalDate.of(2000, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.addUser(user));
        assertEquals("Почта должна быть заполнена и содержать @", exception.getMessage());
    }

    @Test
    public void addUserIncorrectEmail() {
        User user = new User("mail.ru", "user",
                "Name", LocalDate.of(2000, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.addUser(user));
        assertEquals("Почта должна быть заполнена и содержать @", exception.getMessage());
    }

    @Test
    public void addUserWithEmptyLogin() {
        User user = new User("mail@mail.ru", "",
                "Name", LocalDate.of(2000, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.addUser(user));
        assertEquals("Логин не может быть пустым или содержать пробелы", exception.getMessage());
    }

    @Test
    public void addUserWithLoginWithBlank() {
        User user = new User("mail@mail.ru", "us er",
                "Name", LocalDate.of(2000, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.addUser(user));
        assertEquals("Логин не может быть пустым или содержать пробелы", exception.getMessage());
    }

    @Test
    public void addUserWithIncorrectBirthday() {
        User user = new User("mail@mail.ru", "user",
                "Name", LocalDate.of(2030, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.addUser(user));
        assertEquals("Указана некорректная дата рождения", exception.getMessage());
    }

    @Test
    public void updateUserIncorrectEmail() {
        User user1 = new User("mail@mail.ru", "user",
                "Name", LocalDate.of(2000, 1, 1));
        User user2 = new User(1, "mail", "user",
                "Name", LocalDate.of(2000, 1, 1));
        userController.addUser(user1);

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.updateUser(user2));
        assertEquals("Почта должна содержать @", exception.getMessage());
    }

    @Test
    public void updateUserWithEmptyLogin() {
        User user1 = new User("mail@mail.ru", "user",
                "Name", LocalDate.of(2000, 1, 1));
        User user2 = new User(1, "mail2@mail.ru", "",
                "Name2", LocalDate.of(2000, 1, 1));
        userController.addUser(user1);

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.updateUser(user2));
        assertEquals("Логин не может быть пустым или содержать пробелы", exception.getMessage());
    }

    @Test
    public void updateUserWithLoginWithBlank() {
        User user1 = new User("mail@mail.ru", "user",
                "Name", LocalDate.of(2000, 1, 1));
        User user2 = new User(1, "mail2@mail.ru", "u ser",
                "Name2", LocalDate.of(2000, 1, 1));
        userController.addUser(user1);

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.updateUser(user2));
        assertEquals("Логин не может быть пустым или содержать пробелы", exception.getMessage());
    }

    @Test
    public void updateUserWithIncorrectBirthday() {
        User user1 = new User("mail@mail.ru", "user",
                "Name", LocalDate.of(2000, 1, 1));
        User user2 = new User(1, "mail2@mail.ru", "user2",
                "Name2", LocalDate.of(2030, 1, 1));
        userController.addUser(user1);

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.updateUser(user2));
        assertEquals("Указана некорректная дата рождения", exception.getMessage());
    }

    @Test
    public void updateUserWithoutId() {
        User user1 = new User("mail@mail.ru", "user",
                "Name", LocalDate.of(2000, 1, 1));
        User user2 = new User("mail2@mail.ru", "user2",
                "Name2", LocalDate.of(2000, 1, 1));
        userController.addUser(user1);

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.updateUser(user2));
        assertEquals("Id пользователя не может быть пустым", exception.getMessage());
    }

    @Test
    public void updateUserIdNotFound() {
        User user1 = new User("mail@mail.ru", "user",
                "Name", LocalDate.of(2000, 1, 1));
        User user2 = new User(2, "mail2@mail.ru", "user2",
                "Name2", LocalDate.of(2000, 1, 1));
        userController.addUser(user1);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.updateUser(user2));
        assertEquals("Пользователь с id = 2 не найден", exception.getMessage());
    }

    @Test
    public void addUserDuplicateEmail() {
        User user1 = new User("mail@mail.ru", "user1",
                "Name1", LocalDate.of(2000, 1, 1));
        User user2 = new User("mail@mail.ru", "user2",
                "Name2", LocalDate.of(2000, 1, 1));
        userController.addUser(user1);

        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class, () -> userController.addUser(user2));
        assertEquals("Этот имейл уже используется", exception.getMessage());
    }

    @Test
    public void addUserDuplicateLogin() {
        User user1 = new User("mail@mail.ru", "user1",
                "Name1", LocalDate.of(2000, 1, 1));
        User user2 = new User("mail2@mail.ru", "user1",
                "Name2", LocalDate.of(2000, 1, 1));
        userController.addUser(user1);

        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class, () ->
                userController.addUser(user2));
        assertEquals("Этот логин уже используется", exception.getMessage());
    }

    @Test
    public void updateUserDuplicateEmail() {
        User user1 = new User("mail1@mail.ru", "user1",
                "Name1", LocalDate.of(2000, 1, 1));
        User user2 = new User("mail2@mail.ru", "user2",
                "Name2", LocalDate.of(2000, 1, 1));
        User user3 = new User(2, "mail1@mail.ru", "user3",
                "Name3", LocalDate.of(2000, 1, 1));
        userController.addUser(user1);
        userController.addUser(user2);

        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class, () ->
                userController.updateUser(user3));
        assertEquals("Этот имейл уже используется", exception.getMessage());
    }

    @Test
    public void updateUserDuplicateLogin() {
        User user1 = new User("mail1@mail.ru", "user1",
                "Name1", LocalDate.of(2000, 1, 1));
        User user2 = new User("mail2@mail.ru", "user2",
                "Name2", LocalDate.of(2000, 1, 1));
        User user3 = new User(2, "mail3@mail.ru", "user1",
                "Name3", LocalDate.of(2000, 1, 1));
        userController.addUser(user1);
        userController.addUser(user2);

        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class, () ->
                userController.updateUser(user3));
        assertEquals("Этот логин уже используется", exception.getMessage());
    }
}
