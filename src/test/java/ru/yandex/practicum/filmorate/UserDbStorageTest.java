package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    private final JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void setUp(@Autowired JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("INSERT INTO Users(email, login, name, birthday) " +
                "VALUES ('user1@mail.ru', 'login1', 'name1', '1990-01-01')");
        jdbcTemplate.update("INSERT INTO Users(email, login, name, birthday) " +
                "VALUES ('user2@mail.ru', 'login2', 'name2', '1995-02-02')");
        jdbcTemplate.update("INSERT INTO FriendShip(user_id, friend_id) VALUES (1, 2)");
        jdbcTemplate.update("INSERT INTO Users(email, login, name, birthday) " +
                "VALUES ('user3@mail.ru', 'login3', 'name3', '1996-03-03')");
        jdbcTemplate.update("INSERT INTO FriendShip(user_id, friend_id) VALUES (3, 2)");
    }

    @Test
    public void testFindAllUsers() {

        List<User> users = userStorage.findAllUsers();

        assertThat(users).isNotEmpty();
        assertThat(users).size().isEqualTo(3);
    }

    @Test
    public void testFindUserById() {

        Optional<User> userOptional = userStorage.getUser(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                )
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "name1"));
    }

    @Test
    public void testGetFriends() {

        List<User> friends = userStorage.getFriends(1L);

        assertThat(friends).isNotEmpty();
        assertThat(friends).size().isEqualTo(1);
        assertThat(friends.getFirst().getId()).isEqualTo(2);
    }

    @Test
    public void testGetCommonFriends() {

        List<User> friends = userStorage.getCommonFriends(1L, 3L);

        assertThat(friends).isNotEmpty();
        assertThat(friends).size().isEqualTo(1);
        assertThat(friends.getFirst().getId()).isEqualTo(2);
    }

    @Test
    public void testAddUser() {

        User user = new User(null, "user4@mail.ru", "login4", "name4",
                LocalDate.of(1990, 1, 1), Collections.emptySet());
        User result = userStorage.addUser(user);

        assertThat(result.getId()).isEqualTo(4);
        assertThat(result.getLogin()).isEqualTo("login4");
    }

    @Test
    public void testUpdateUser() {

        User user = new User(2L, "user10@mail.ru", "login10", "name10",
                LocalDate.of(1990, 1, 1), Collections.emptySet());
        User result = userStorage.updateUser(user);

        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getLogin()).isEqualTo("login10");
        assertThat(result.getEmail()).isEqualTo("user10@mail.ru");
        assertThat(result.getName()).isEqualTo("name10");
    }

    @Test
    public void testAddFriend() {

        User result = userStorage.addFriend(2L, 1L);
        List<User> friends = userStorage.getFriends(2L);

        assertThat(friends).isNotEmpty();
        assertThat(friends).size().isEqualTo(1);
        assertThat(friends.getFirst().getId()).isEqualTo(1);
    }

    @Test
    public void testDeleteFriend() {

        User result = userStorage.deleteFriend(1L, 2L);
        List<User> friends = userStorage.getFriends(1L);

        assertThat(friends).isEmpty();
    }
}