package ru.yandex.practicum.filmorate.dal;

import jakarta.transaction.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    private static final String DELETE_USER_QUERY = "DELETE FROM Users WHERE id = ?";

    public UserDbStorage(JdbcTemplate jdbc) {
        super(jdbc);
    }

    public List<User> findAllUsers() {
        List<Long> userIds = jdbc.query("SELECT id FROM Users",
                (rs, rn) -> rs.getLong("id"));
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Set<Long>> friends = findFriends(userIds);
        RowMapper<User> mapper = new UserRowMapper(friends);
        return findMany(FIND_ALL_QUERY, mapper);
    }

    public Optional<User> getUser(Long id) {
        Map<Long, Set<Long>> friends = findFriends(Collections.singletonList(id));
        RowMapper<User> mapper = new UserRowMapper(friends);
        return findOne(FIND_BY_ID_QUERY, mapper, id);
    }

    public List<User> getFriends(Long id) {
        Map<Long, Set<Long>> friends = findFriends(Collections.singletonList(id));
        RowMapper<User> mapper = new UserRowMapper(friends);
        return findMany(FIND_FRIENDS_QUERY, mapper, id);
    }

    public List<User> getCommonFriends(Long user1Id, Long user2Id) {
        List<Long> userIds = jdbc.query(FIND_COMMON_FRIENDS_QUERY,
                (rs, rn) -> rs.getLong("id"), user1Id, user2Id);
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Set<Long>> friends = findFriends(userIds);
        RowMapper<User> mapper = new UserRowMapper(friends);
        return findMany(FIND_COMMON_FRIENDS_QUERY, mapper, user1Id, user2Id);
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
        return getUser(userId).orElseThrow();
    }

    public User deleteFriend(Long userId, Long friendId) {
        delete(
                DELETE_FRIEND_QUERY,
                userId,
                friendId
        );
        return getUser(userId).orElseThrow();
    }

    @Transactional
    public void deleteUser(Long id) {
        delete(
                DELETE_USER_QUERY,
                id
        );
    }

    private Map<Long, Set<Long>> findFriends(Collection<Long> userIds) {
        String query = "SELECT * FROM FriendShip WHERE user_id IN (" +
                userIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";
        Map<Long, Set<Long>> result = new HashMap<>();
        jdbc.query(query, rs -> {
            Long userId = rs.getLong("user_id");
            Long friendId = rs.getLong("friend_id");
            result.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        });
        return result;
    }
}
