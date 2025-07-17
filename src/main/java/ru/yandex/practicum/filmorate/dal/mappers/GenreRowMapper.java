package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreRowMapper implements RowMapper<Genre> {
    private final JdbcTemplate jbdc;

    public GenreRowMapper(JdbcTemplate jbdc) {
        this.jbdc = jbdc;
    }

    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Genre.fromId(rs.getInt("id"));
    }
}
