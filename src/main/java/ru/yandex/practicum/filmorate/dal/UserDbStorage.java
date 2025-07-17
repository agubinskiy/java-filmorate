package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Repository("userDboStorage")
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM Users ORDER BY id ASC";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM Users WHERE id = ?";
    private static final String FIND_FRIENDS_QUERY =
            "SELECT * FROM Users WHERE id IN (SELECT friend_id FROM FriendShip WHERE user_id = ?)";
    private static final String FIND_COMMON_FRIENDS_QUERY =
            "SELECT * FROM Users WHERE id IN (SELECT friend_id FROM FriendShip WHERE user_id = ? INTERSECT " +
                    "SELECT friend_id FROM FriendShip WHERE user_id = ?)";
    private static final String INSERT_USER_QUERY = "INSERT INTO Users(email, login, name, birthday) " +
            "VALUES(?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE Users SET email = ?, login = ?, name = ?, " +
            "birthday = ? WHERE id = ?";
    private static final String INSERT_FRIEND_QUERY = "INSERT INTO FriendShip(user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM FriendShip WHERE user_id = ? AND friend_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public List<User> findAllUsers() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<User> getUser(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<User> getFriends(Long id) {
        return findMany(FIND_FRIENDS_QUERY, id);
    }

    public List<User> getCommonFriends(Long user1Id, Long user2Id) {
        return findMany(FIND_COMMON_FRIENDS_QUERY, user1Id, user2Id);
    }

    public User addUser(User user) {
        long id = insert(
                INSERT_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    public User updateUser(User user) {
        update(
                UPDATE_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    public User addFriend(Long userId, Long friendId) {
        insert(
                INSERT_FRIEND_QUERY,
                userId,
                friendId
        );
        return getUser(userId).get();
    }

    public User deleteFriend(Long userId, Long friendId) {
        delete(
                DELETE_FRIEND_QUERY,
                userId,
                friendId
        );
        return getUser(userId).get();
    }
}
