package ru.yandex.practicum.filmorate.comparator;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class FilmComparatorLikes implements Comparator<Film> {

    @Override
    public int compare(Film f1, Film f2) {
        int likesCountF1 = 0;
        int likesCountF2 = 0;

        // Проверяем, что у первого фильма есть лайки
        if (f1.getLikes() != null) {
            // Если лайки не null, берем их размер
            likesCountF1 = f1.getLikes().size();
        } else {
            likesCountF1 = 0;
        }

        if (f2.getLikes() != null) {
            likesCountF2 = f2.getLikes().size();
        } else {
            likesCountF2 = 0;
        }

        return Integer.compare(likesCountF2, likesCountF1);
    }
}
