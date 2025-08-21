package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class UserRowMapper implements RowMapper<User> {
    private final Map<Long, Set<Long>> friends;

    public UserRowMapper(Map<Long, Set<Long>> friends) {
        this.friends = friends;
    }

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        Long userId = resultSet.getLong("id");
        user.setId(userId);
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));
        user.setBirthday(resultSet.getDate("birthday").toLocalDate());

        user.setFriends(friends.getOrDefault(userId, Collections.emptySet()));

        return user;
    }
}
