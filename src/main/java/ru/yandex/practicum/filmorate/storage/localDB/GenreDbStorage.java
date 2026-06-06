package ru.yandex.practicum.filmorate.storage.localDB;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Repository
public class GenreDbStorage implements GenreStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    private static final String FIND_ONE_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String FIND_ALL_BY_IDS_QUERY = """
            SELECT *
            FROM genres
            WHERE id in (%s)
            """;
    private static final String FIND_ALL_GENRES_BY_FILM_ID = """
            SELECT g.*
            FROM genres g
            JOIN film_genre fg ON g.id = fg.genre_id
            WHERE fg.film_id = ?
    """;

    private final JdbcTemplate jdbc;
    private final static GenreMapper mapper = new GenreMapper();

    @Override
    public Collection<Genre> findAll() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    @Override
    public Optional<Genre> findOne(Long id) {
        try {
            log.trace("Find one genre initiated");
            return Optional.ofNullable(jdbc.queryForObject(FIND_ONE_QUERY, mapper, id));
        } catch (EmptyResultDataAccessException e) {
            log.info("Genre with id {} was not found", id);
            return Optional.empty();
        }
    }

    @Override
    public Genre create(Genre entity) {
        return null;
    }

    @Override
    public Genre update(Genre entity) {
        return null;
    }

    @Override
    public Genre delete(Long id) {
        return null;
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        log.info("FIND ALL BY IDS: {}", ids);

        String placeholders = ids.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        String sqlQuery = FIND_ALL_BY_IDS_QUERY.formatted(placeholders);

        return jdbc.query(sqlQuery, mapper, ids.toArray());
    }

    @Override
    public List<Genre> findAllGenresOfFilmId(long filmId) {
        return jdbc.query(FIND_ALL_GENRES_BY_FILM_ID, mapper, filmId);
    }
}
