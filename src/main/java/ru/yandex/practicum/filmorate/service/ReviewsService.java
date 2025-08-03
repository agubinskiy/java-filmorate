package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.EventDBStorage;
import ru.yandex.practicum.filmorate.dal.ReviewsDbStorage;
import ru.yandex.practicum.filmorate.dto.EventType;
import ru.yandex.practicum.filmorate.dto.OperationType;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ReviewsService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final ReviewsDbStorage reviewStorage;
    private final EventDBStorage eventStorage;

    @Autowired
    public ReviewsService(@Qualifier("filmDboStorage") FilmStorage filmStorage,
                          @Qualifier("userDboStorage") UserStorage userStorage,
                          @Qualifier("reviewsDbStorage") ReviewsDbStorage reviewStorage,
                          @Qualifier("eventDBStorage")EventDBStorage eventStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.reviewStorage = reviewStorage;
        this.eventStorage = eventStorage;
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
        ReviewDto newReview = reviewStorage.addReview(review);
        eventStorage.addEvent(Event.builder()
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(OperationType.ADD)
                .timestamp(Instant.now().toEpochMilli())
                .entityId(newReview.getReviewId())
                .build());
        return newReview;
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
        eventStorage.addEvent(Event.builder()
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(OperationType.UPDATE)
                .timestamp(Instant.now().toEpochMilli())
                .entityId(review.getReviewId())
                .build());
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
            eventStorage.addEvent(Event.builder()
                    .userId(reviewStorage.findReviewById(reviewId).get().getUserId())
                    .eventType(EventType.REVIEW)
                    .operation(OperationType.REMOVE)
                    .timestamp(Instant.now().toEpochMilli())
                    .entityId(reviewId)
                    .build());
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

    public void addLike(Long reviewId, Long userId, int isPositive) {
        if (reviewStorage.findReviewById(reviewId).isEmpty()) {
            log.warn("Ошибка при лайке отзыва. Отзыв с id={} не найден", reviewId);
            throw new NotFoundException("Отзыв с id=" + reviewId + " не найден.");
        }
        if (userStorage.getUser(userId).isEmpty()) {
            log.warn("Ошибка при  добавлении лайка. Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        reviewStorage.deleteLike(reviewId, userId);
        reviewStorage.addLike(isPositive, reviewId, userId);
        eventStorage.addEvent(Event.builder()
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(OperationType.ADD)
                .timestamp(Instant.now().toEpochMilli())
                .entityId(reviewId)
                .build());
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
        eventStorage.addEvent(Event.builder()
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(OperationType.REMOVE)
                .timestamp(Instant.now().toEpochMilli())
                .entityId(reviewId)
                .build());
        reviewStorage.deleteLike(reviewId, userId);
    }


}

