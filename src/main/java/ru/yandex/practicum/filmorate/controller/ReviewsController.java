package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.service.ReviewsService;

import java.util.List;


@RestController
@RequestMapping("/reviews")
@Slf4j
public class ReviewsController {
    private final ReviewsService reviewsService;

    public ReviewsController(ReviewsService reviewsService) {
        this.reviewsService = reviewsService;
    }

    @PostMapping
    public ReviewDto addReview(@Valid @RequestBody ReviewDto review) {
        log.info("Добавляется отзыв на фильм {} пользователем {}", review.getFilmId(), review.getUserId());
        return reviewsService.addReview(review);
    }

    @PutMapping
    public ReviewDto updateReview(@Valid @RequestBody ReviewDto review) {
        log.info("Обновляется отзыв {}", review.getReviewId());
        return reviewsService.updateReview(review);
    }

    @GetMapping("/{id}")
    public ReviewDto getReview(@PathVariable Long id) {
        log.info("Запрошена информация по отзыву id={}", id);
        return reviewsService.getReview(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        log.info("Удаляется отзыв id={}", id);
        reviewsService.deleteReview(id);
    }

    @GetMapping
    public List<ReviewDto> getFilmReviews(@RequestParam(defaultValue = "10") int count,
                                          @RequestParam(defaultValue = "-1") Long filmId) {
        log.info("Запрошен отзывы по фильму id={}", filmId);
        return reviewsService.getFilmReviews(count, filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Добавляется лайк отзыву id={}", id);
        reviewsService.addLike(id, userId, 1);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDisLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Добавляется  дизлайк отзыву id={}", id);
        reviewsService.addLike(id, userId, -1);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Удаляется лайк отзыву id={}", id);
        reviewsService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDisLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Удаляется дизлайк отзыву id={}", id);
        reviewsService.deleteLike(id, userId);
    }
}
