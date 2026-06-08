package ru.yandex.practicum.filmorate.storage.localDB;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MpaDbStorage implements MpaStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa";
    private static final String FIND_ONE_QUERY = "SELECT * FROM mpa WHERE id = ?";

    private final JdbcTemplate jdbc;
    private static final MpaMapper mapper = new MpaMapper();

    @Override
    public Collection<Mpa> findAll() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    @Override
    public Optional<Mpa> findOne(Long id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(FIND_ONE_QUERY, mapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Mpa create(Mpa entity) {
        return null;
    }

    @Override
    public Mpa update(Mpa entity) {
        return null;
    }

    @Override
    public Mpa delete(Long id) {
        return null;
    }

}
