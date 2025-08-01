package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.ReviewsDbStorage;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ReviewsService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final ReviewsDbStorage reviewStorage;

    @Autowired
    public ReviewsService(@Qualifier("filmDboStorage") FilmStorage filmStorage,
                          @Qualifier("userDboStorage") UserStorage userStorage,
                          @Qualifier("reviewsDbStorage") ReviewsDbStorage reviewStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.reviewStorage = reviewStorage;
    }

    public ReviewDto addReview(@Valid ReviewDto review) {
        if (userStorage.getUser(review.getUserId()).isEmpty()) {
            log.warn("Ошибка при добавлении отзыва. Пользователь с id={} не найден", review.getUserId());
            throw new NotFoundException("Пользователь с id=" + review.getUserId() + " не найден");
        }
        if (filmStorage.getFilm(review.getFilmId()).isEmpty()) {
            log.warn("Ошибка при добавлении отзыва. Фильм с id={} не найден", review.getFilmId());
            throw new NotFoundException("Фильм с id=" + review.getFilmId() + " не найден");
        }
        return reviewStorage.addReview(review);
    }

    public ReviewDto updateReview(@Valid ReviewDto review) {
        if (userStorage.getUser(review.getUserId()).isEmpty()) {
            log.warn("Ошибка при обновлении отзыва. Пользователь с id={} не найден", review.getUserId());
            throw new NotFoundException("Пользователь с id=" + review.getUserId() + " не найден");
        }
        if (filmStorage.getFilm(review.getFilmId()).isEmpty()) {
            log.warn("Ошибка при обновлении отзыва. Фильм с id={} не найден", review.getFilmId());
            throw new NotFoundException("Фильм с id=" + review.getFilmId() + " не найден");
        }
        return reviewStorage.updateReview(review);
    }

    public Optional<ReviewDto> getReview(Long reviewId) {
        Optional<ReviewDto> reviewDto = reviewStorage.findReviewById(reviewId);
        if (reviewDto.isEmpty()) {
            throw new NotFoundException("Отзыв с id=" + reviewId + " не найден");
        }
        return reviewDto;
    }

    public void deleteReview(Long reviewId) {
        if (reviewStorage.findReviewById(reviewId).isPresent()) {
            reviewStorage.deleteReview(reviewId);
        } else {
            log.warn("Ошибка при удалении отзыва. Отзыв с id={} не найден", reviewId);
            throw new NotFoundException("Отзыв с id=" + reviewId + " не найден.");
        }
    }

    public List<ReviewDto> getFilmReviews(Integer count, Long filmId) {
        log.info("Запрошен отзывы 1 по фильму id={}", filmId);
        return reviewStorage.getFilmReviews(count, filmId);
    }

    public void addLike(Long reviewId, Long userId, int IsPositive) {
        if (reviewStorage.findReviewById(reviewId).isEmpty()) {
            log.warn("Ошибка при лайке отзыва. Отзыв с id={} не найден", reviewId);
            throw new NotFoundException("Отзыв с id=" + reviewId + " не найден.");
        }
        if (userStorage.getUser(userId).isEmpty()) {
            log.warn("Ошибка при  добавлении лайка. Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        reviewStorage.deleteLike(reviewId, userId);
        reviewStorage.addLike(IsPositive, reviewId, userId);
    }

    public void deleteLike(Long reviewId, Long userId) {
        if (reviewStorage.findReviewById(reviewId).isEmpty()) {
            log.warn("Ошибка при удалении лайка отзыва. Отзыв с id={} не найден", reviewId);
            throw new NotFoundException("Отзыв с id=" + reviewId + " не найден.");
        }
        if (userStorage.getUser(userId).isEmpty()) {
            log.warn("Ошибка при удалении лайка. Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        reviewStorage.deleteLike(reviewId, userId);
    }


}

