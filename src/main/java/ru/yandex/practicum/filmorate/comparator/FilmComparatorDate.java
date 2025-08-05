package ru.yandex.practicum.filmorate.comparator;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Comparator;

public class FilmComparatorDate implements Comparator<Film> {

    @Override
    public int compare(Film f1, Film f2) {
        if (f1.getReleaseDate() == null && f2.getReleaseDate() == null) {
            return 0;
        } else if (f1.getReleaseDate() == null) {
            return 1;
        } else if (f2.getReleaseDate() == null) {
            return -1;
        } else {
            return f1.getReleaseDate().compareTo(f2.getReleaseDate());
        }
    }
}
