package ru.yandex.practicum.filmorate.storage.localDB;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_ONE_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_FILM_QUERY = "INSERT INTO films(name, description, duration, release_date)" +
            "VALUES(?, ?, ?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, duration = ?, release_date = ? WHERE id = ?";


    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Film> findAll() {
        return findAll(FIND_ALL_QUERY);
    }

    public Optional<Film> findOne(Long id) {
        return findOne(FIND_ONE_QUERY, id);
    }

    public Film create(Film film) {
        long id = super.create(INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate());
        film.setId(id);
        return film;
    }

    public Film update(Film film) {
        update(UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate());
        return film;
    }
}
