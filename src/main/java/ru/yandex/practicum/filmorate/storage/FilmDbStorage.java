package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

@Repository
public class FilmDbStorage extends BaseStorage<Film> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_ONE_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_FILM_QUERY = "INSERT INTO films(name, description, release_date, duration)" +
            "VALUES(?, ?, ?, ?) RETURNING id";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?";


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
        long id = insert(INSERT_FILM_QUERY, film);
        film.setId(id);
        return film;
    }

    public Film update(Film film) {
        update(UPDATE_FILM_QUERY, film);
        return film;
    }
}
