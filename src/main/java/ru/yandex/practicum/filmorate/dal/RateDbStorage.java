package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.RateRowMapper;
import ru.yandex.practicum.filmorate.model.Rate;
import ru.yandex.practicum.filmorate.storage.RateStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class RateDbStorage extends BaseDbStorage<Rate> implements RateStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM Rates";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM Rates WHERE id = ?";
    private static final RowMapper<Rate> mapper = new RateRowMapper();

    public RateDbStorage(JdbcTemplate jdbc) {
        super(jdbc);
    }

    public List<Rate> findAllRates() {
        return findMany(FIND_ALL_QUERY, mapper);
    }

    public Optional<Rate> getRate(Integer id) {
        return findOne(FIND_BY_ID_QUERY, mapper, id);
    }
}
