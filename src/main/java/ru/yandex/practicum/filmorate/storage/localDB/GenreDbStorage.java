package ru.yandex.practicum.filmorate.storage.localDB;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    private static final String FIND_ONE_QUERY = "SELECT * FROM genres WHERE id = ?";

    private final JdbcTemplate jdbc;
    private final GenreMapper mapper = new GenreMapper();

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
}
