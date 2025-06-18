package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
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
        User user = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .login("user")
                .name("Name")
                .birthday(LocalDate.of(2000, 5, 10))
                .build();
        userController.addUser(user);

        assertNotNull(userController.getUsers());
        assertEquals(1, userController.getUsers().size());
        assertEquals("user", userController.getUsers().get(1L).getLogin(),
                "Логин пользователя некорректен");
    }

    @Test
    public void updateUser() {
        User user1 = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .login("user")
                .name("Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User user2 = User.builder()
                .id(1L)
                .email("mail2@mail.ru")
                .login("user2")
                .name("Name2")
                .birthday(LocalDate.of(2000, 2, 2))
                .build();
        userController.addUser(user1);
        userController.updateUser(user2);

        assertNotNull(userController.getUsers());
        assertEquals(1, userController.getUsers().size());
        assertEquals("user2", userController.getUsers().get(1L).getLogin(),
                "Логин пользователя некорректен");
    }

    @Test
    public void getAllUsers() {
        User user1 = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .login("user")
                .name("Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User user2 = User.builder()
                .id(1L)
                .email("mail2@mail.ru")
                .login("user2")
                .name("Name2")
                .birthday(LocalDate.of(2000, 2, 2))
                .build();
        userController.addUser(user1);
        userController.addUser(user2);
        Collection<User> result = userController.findAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userController.getUsers().values(), result,
                "Список пользователей некорректен");
    }

    @Test
    public void updateUserIdNotFound() {
        User user1 = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .login("user")
                .name("Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("mail2@mail.ru")
                .login("user2")
                .name("Name2")
                .birthday(LocalDate.of(2000, 2, 2))
                .build();
        userController.addUser(user1);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.updateUser(user2));
        assertEquals("Пользователь с id = 2 не найден", exception.getMessage());
    }

    @Test
    public void addUserDuplicateEmail() {
        User user1 = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .login("user")
                .name("Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("mail@mail.ru")
                .login("user2")
                .name("Name2")
                .birthday(LocalDate.of(2000, 2, 2))
                .build();
        userController.addUser(user1);

        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class,
                () -> userController.addUser(user2));
        assertEquals("Этот имейл уже используется", exception.getMessage());
    }

    @Test
    public void addUserDuplicateLogin() {
        User user1 = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .login("user")
                .name("Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("mail2@mail.ru")
                .login("user")
                .name("Name2")
                .birthday(LocalDate.of(2000, 2, 2))
                .build();
        userController.addUser(user1);

        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class, () ->
                userController.addUser(user2));
        assertEquals("Этот логин уже используется", exception.getMessage());
    }

    @Test
    public void updateUserDuplicateEmail() {
        User user1 = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .login("user")
                .name("Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("mail2@mail.ru")
                .login("user2")
                .name("Name2")
                .birthday(LocalDate.of(2000, 2, 2))
                .build();
        User user3 = User.builder()
                .id(2L)
                .email("mail@mail.ru")
                .login("user3")
                .name("Name3")
                .birthday(LocalDate.of(2000, 3, 3))
                .build();
        userController.addUser(user1);
        userController.addUser(user2);

        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class, () ->
                userController.updateUser(user3));
        assertEquals("Этот имейл уже используется", exception.getMessage());
    }

    @Test
    public void updateUserDuplicateLogin() {
        User user1 = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .login("user")
                .name("Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("mail2@mail.ru")
                .login("user2")
                .name("Name2")
                .birthday(LocalDate.of(2000, 2, 2))
                .build();
        User user3 = User.builder()
                .id(2L)
                .email("mail3@mail.ru")
                .login("user")
                .name("Name3")
                .birthday(LocalDate.of(2000, 3, 3))
                .build();
        userController.addUser(user1);
        userController.addUser(user2);

        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class, () ->
                userController.updateUser(user3));
        assertEquals("Этот логин уже используется", exception.getMessage());
    }
}
