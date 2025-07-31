package ru.yandex.practicum.filmorate.recommender;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FilmsRecommender {
    private final Map<Long, Map<Long, Double>> data;
    private final Map<Long, Map<Long, Double>> diff = new HashMap<>();
    private final Map<Long, Map<Long, Integer>> freq = new HashMap<>();

    public FilmsRecommender(Map<Long, Map<Long, Double>> data) {
        this.data = data;
    }

    public List<Long> getRecommendation(Long userId) {
        Map<Long, Double> userRatings = data.getOrDefault(userId, Collections.emptyMap());
        build(data);
        Map<Long, Double> predictions = predict(userRatings);

        return predictions.entrySet().stream()
                .filter(e -> !userRatings.containsKey(e.getKey()))
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Entry::getKey)
                .toList();
    }

    private void build(Map<Long, Map<Long, Double>> data) {
        //Заполняем матрицы разностей и частот
        for(Map<Long, Double> user: data.values()) {
            for (Entry<Long, Double> e : user.entrySet()) {
                //Если значения для фильма еще нет, то добавляем его
                Long filmId1 = e.getKey();
                    diff.computeIfAbsent(filmId1, k -> new HashMap<>());
                    freq.computeIfAbsent(filmId1, k -> new HashMap<>());

                //Проходим по всем фильмам. Если есть оценка - увеличиваем число оценок и общий рейтинг
                for (Entry<Long, Double> e2 : user.entrySet()) {
                    Long filmId2 = e2.getKey();
                    if(filmId2.equals(filmId1)) continue;

                    double observedDiff = e.getValue() - e2.getValue();
                    freq.get(filmId1).merge(filmId2, 1, Integer::sum);
                    diff.get(filmId1).merge(filmId2, observedDiff, Double::sum);
                }
            }
        }
        //Для каждого фильма считаем средний рейтинг(для лайков всегда = 1)
        for (Long j : diff.keySet()) {
            for (Long i : diff.get(j).keySet()) {
                double oldValue = diff.get(j).get(i);
                int count = freq.get(j).get(i);
                diff.get(j).put(i, oldValue / count);
            }
        }
    }

    private Map<Long, Double> predict(Map<Long, Double> userFilms) {
        Map<Long, Double> predictions = new HashMap<>();
        Map<Long, Integer> frequencies = new HashMap<>();

        //Проходим по всем оценкам пользователя
        for (Entry<Long, Double> e : userFilms.entrySet()) {
            Long userFilmId = e.getKey();
            Double rate = e.getValue();

            //Исключаем фильмы, о которых нет информации
            if (!diff.containsKey(userFilmId)) continue;

            //Проходим по всем фильмам из матрицы отклонений
            for (Entry<Long, Double> item : diff.get(userFilmId).entrySet()) {
                int count = freq.get(userFilmId).get(item.getKey());

                //Считаем оценку
                double predicatedRating = rate + item.getValue();
                double weightedRating = predicatedRating * count;

                //Заполняем результаты
                predictions.merge(item.getKey(), weightedRating, Double::sum);
                frequencies.merge(item.getKey(), count, Integer::sum);
            }
        }

            Map<Long, Double> result = new HashMap<>();
            for (Long j : predictions.keySet()) {
                    result.put(j, predictions.get(j) / frequencies.get(j));
            }

            return result;
        }
}
