package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;

@Repository("directorDbStorage")
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {

    private static final String INSERT_QUERY = "INSERT INTO directors(name) VALUES (?)";

    private static final String FIND_ALL_QUERY =
            "SELECT d.directors_id, d.name, " +
                    "FROM directors d " +
                    "GROUP BY d.directors_id, d.name";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE directors_id = ?";

    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE directors_id = ?";

    private static final String DELETE_DIRECTOR = "DELETE FROM directors WHERE directors_id = ?";

    public DirectorDbStorage(JdbcTemplate jdbc) {
        super(jdbc);
    }

    public List<Director> findAll() {
        RowMapper<Director> mapper = new DirectorRowMapper();
        return findMany(FIND_ALL_QUERY, mapper);
    }

    public Optional<Director> findById(long id) {
        RowMapper<Director> mapper = new DirectorRowMapper();
        return findOne(FIND_BY_ID_QUERY, mapper, id);
    }

    public List<Director> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        String directorsIds = "";
        for (int i = 0; i < ids.size(); i++) {
            directorsIds += "?";
            if (i < ids.size() - 1) {
                directorsIds += ",";
            }
        }

        String listDirectorsId = "SELECT * FROM directors WHERE directors_id IN (" + directorsIds + ")";
        return jdbc.query(listDirectorsId, ids.toArray(), new DirectorRowMapper());
    }

    public boolean deleteFilmDirector(long directorId) {
        return delete(DELETE_DIRECTOR, directorId);
    }

    public Director save(Director director) {

        long id = insert(
                INSERT_QUERY,
                director.getName()
        );
        director.setId(id);

        return director;
    }

    public Director update(Director director) {
        update(
                UPDATE_QUERY,
                director.getName(),
                director.getId()
        );
        return director;
    }

}
