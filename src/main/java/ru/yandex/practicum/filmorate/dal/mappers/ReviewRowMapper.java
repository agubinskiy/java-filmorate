package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dto.ReviewDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewRowMapper implements RowMapper<ReviewDto> {
    @Override
    public ReviewDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setReviewId(resultSet.getLong("reviewId"));
        reviewDto.setContent(resultSet.getString("content"));
        reviewDto.setIsPositive(resultSet.getBoolean("isPositive"));
        reviewDto.setUserId(resultSet.getLong("userId"));
        reviewDto.setFilmId(resultSet.getLong("filmId"));
        reviewDto.setUseful(resultSet.getInt("useful"));

        return reviewDto;
    }
}
