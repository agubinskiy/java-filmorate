package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rate;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RateRowMapper implements RowMapper<Rate> {
    @Override
    public Rate mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Rate.fromId(rs.getInt("id"));
    }
}
