package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.dto.ReviewDto;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository("reviewsDbStorage")
public class ReviewsDbStorage {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static final String INSERT_REVIEW_QUERY = "INSERT INTO Reviews (content, isPositive, userId, filmId) " +
            "VALUES(?, ?, ?, ?)";
    private static final String UPDATE_REVIEW_QUERY = "UPDATE Reviews SET content = ?, isPositive = ? " +
            "WHERE reviewId = ?";
    private static final String DELETE_REVIEW_QUERY = "DELETE FROM Reviews WHERE reviewId = ? ";
    private static final String GET_REVIEW_QUERY = "SELECT * FROM Reviews WHERE reviewId = ? ";
    private static final String GET_REVIEWBYFILM_QUERY = "SELECT * FROM Reviews WHERE filmId = ? ORDER BY useful desc LIMIT ?";
    private static final String GET_ALLREVIEWS_QUERY = "SELECT * FROM Reviews ORDER BY useful desc LIMIT ?";
    private static final String ADD_LIKE_QUERY = "INSERT INTO ReviewsScore (isPositive, userId, reviewsId) " +
            "VALUES(?, ?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM ReviewsScore WHERE reviewsId = ? and userId = ?";
    private static final String UPDATE_USEFUL_QUERY = "UPDATE Reviews SET " +
            "useful = (SELECT NVL(SUM(s.IsPositive), 0) FROM REVIEWSSCORE s where s.reviewsId = ?) WHERE reviewId = ? ";



    public ReviewDto addReview(ReviewDto reviewDto) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT_REVIEW_QUERY, new String[]{"reviewId"});
            stmt.setString(1, reviewDto.getContent());
            stmt.setBoolean(2, reviewDto.getIsPositive());
            stmt.setLong(3, reviewDto.getUserId());
            stmt.setLong(4, reviewDto.getFilmId());
            return stmt;
        }, keyHolder);
        long reviewId = keyHolder.getKey().longValue();
        reviewDto.setReviewId(reviewId);
        reviewDto.setUseful(0);
        return reviewDto;
    }

    public ReviewDto updateReview(ReviewDto review) {

        jdbcTemplate.update(UPDATE_REVIEW_QUERY,
                review.getContent(), review.getIsPositive(), review.getReviewId());
        return findReviewById(review.getReviewId()).orElse(null);
    }

    public void deleteReview(Long reviewId) {
        jdbcTemplate.update(DELETE_REVIEW_QUERY, reviewId);
    }

    public Optional<ReviewDto> findReviewById(long reviewId) {
        ReviewRowMapper mapper = new ReviewRowMapper();
        List<ReviewDto> reviews = jdbcTemplate.query(GET_REVIEW_QUERY, mapper, reviewId);
        if (reviews.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(reviews.getFirst());
        }
    }

    public List<ReviewDto> getFilmReviews(Integer count, Long filmId) {
        ReviewRowMapper mapper = new ReviewRowMapper();
        return jdbcTemplate.query(GET_REVIEWBYFILM_QUERY, mapper, filmId, count);
    }

    public List<ReviewDto> getAllReviews(Integer count) {
        ReviewRowMapper mapper = new ReviewRowMapper();
        return jdbcTemplate.query(GET_ALLREVIEWS_QUERY, mapper, count);
    }

    public void addLike(int isPositive, Long reviewId, Long userId) {
        jdbcTemplate.update(ADD_LIKE_QUERY, isPositive, userId, reviewId);
        jdbcTemplate.update(UPDATE_USEFUL_QUERY, reviewId, reviewId);
    }

    public void deleteLike(Long reviewId, Long userId) {
        jdbcTemplate.update(DELETE_LIKE_QUERY, reviewId, userId);
        jdbcTemplate.update(UPDATE_USEFUL_QUERY, reviewId, reviewId);
    }
}

