package ru.yandex.practicum.filmorate.storage.localDB;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_ONE_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_FILM_QUERY = "INSERT INTO films(name, description, duration, release_date)" +
            "VALUES(?, ?, ?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, duration = ?, release_date = ? WHERE id = ?";
    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE id = ?";


    private static final FilmMapper mapper = new FilmMapper();

    private final JdbcTemplate jdbc;

    public Collection<Film> findAll() {
        return jdbc.query(FIND_ALL_QUERY, mapper).stream().toList();
    }

    public Optional<Film> findOne(Long id) {
        try {
            log.trace("Searching for film with id {}", id);
            Film film = jdbc.queryForObject(FIND_ONE_QUERY, mapper, id);
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            log.info("Film with id {} is not found", id);
            return Optional.empty();
        }
    }

    public Film create(Film film) {
        log.trace("Create film initiated");
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(INSERT_FILM_QUERY, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setLong(3, film.getDuration());
            ps.setDate(4, Date.valueOf(film.getReleaseDate()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() == null) {
            return null;
        }
        Long id = keyHolder.getKey().longValue();
        film.setId(id);
        return film;
    }

    public Film update(Film film) {
        log.trace("Update film initiated");
        Long id = film.getId();
        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(UPDATE_FILM_QUERY);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setLong(3, film.getDuration());
            ps.setDate(4, Date.valueOf(film.getReleaseDate()));
            ps.setLong(5, id);
            return ps;
        });
        return film;
    }

    @Override
    public Film delete(Long id) {
        log.trace("Delete film initiated");
        Optional<Film> deletedFilm = findOne(id);
        if (deletedFilm.isPresent()) {
            jdbc.update(DELETE_FILM_QUERY, mapper, id);
        }
        return deletedFilm.orElse(null);
    }
}
