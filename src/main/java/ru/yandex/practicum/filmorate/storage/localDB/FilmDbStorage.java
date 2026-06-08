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
import java.sql.Types;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private static final String FILM_WITH_MPA_COLUMNS = """
            f.id, f.name, f.description, f.release_date, f.duration,
            m.id AS mpa_id, m.name AS mpa_name
            """;
    private static final String FIND_ALL_QUERY = """
            SELECT %s
            FROM films f
            LEFT JOIN mpa m ON f.mpa_id = m.id
            """.formatted(FILM_WITH_MPA_COLUMNS);
    private static final String FIND_ONE_QUERY = """
            SELECT %s
            FROM films f
            LEFT JOIN mpa m ON f.mpa_id = m.id
            WHERE f.id = ?
            """.formatted(FILM_WITH_MPA_COLUMNS);
    private static final String INSERT_FILM_QUERY = """
            INSERT INTO films(name, description, duration, release_date, mpa_id)
            VALUES(?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_FILM_QUERY = """
            UPDATE films SET name = ?, description = ?, duration = ?, release_date = ?, mpa_id = ?
            WHERE id = ?
            """;
    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String INSERT_LIKE_QUERY = """
            INSERT INTO film_likes(film_id, user_id)
            VALUES(?, ?)
            """;
    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM film_likes
            WHERE film_id = ? AND user_id = ?
            """;
    private static final String FIND_POPULAR_QUERY = """
            SELECT f.id, f.name, f.description, f.release_date, f.duration,
                   m.id AS mpa_id, m.name AS mpa_name
            FROM films f
            LEFT JOIN mpa m ON f.mpa_id = m.id
            INNER JOIN film_likes fl ON f.id = fl.film_id
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.id, m.name
            ORDER BY COUNT(fl.user_id) DESC
            LIMIT ?
            """;
    private static final String INSERT_FILM_GENRE_CONN_QUERY = """
            INSERT INTO film_genre(film_id, genre_id)
            VALUES (?, ?)
            """;
    private static final FilmMapper mapper = new FilmMapper();

    private final JdbcTemplate jdbc;

    public Collection<Film> findAll() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
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
        KeyHolder keyHolderFilm = new GeneratedKeyHolder();
        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(INSERT_FILM_QUERY, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setLong(3, film.getDuration());
            ps.setDate(4, Date.valueOf(film.getReleaseDate()));
            if (film.getMpa() != null && film.getMpa().getId() != null) {
                ps.setLong(5, film.getMpa().getId());
            } else {
                ps.setNull(5, Types.BIGINT);
            }
            return ps;
        }, keyHolderFilm);
        if (keyHolderFilm.getKey() == null) {
            return null;
        }
        Long id = keyHolderFilm.getKey().longValue();
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
            if (film.getMpa() != null && film.getMpa().getId() != null) {
                ps.setLong(5, film.getMpa().getId());
            } else {
                ps.setNull(5, Types.BIGINT);
            }
            ps.setLong(6, id);
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

    @Override
    public boolean addLike(Long filmId, Long userId) {
        log.trace("Add like initiated");
        int rowsAffected = jdbc.update(INSERT_LIKE_QUERY, filmId, userId);
        return rowsAffected > 0;
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {
        log.trace("Remove like initiated");
        int rowsAffected = jdbc.update(DELETE_LIKE_QUERY, filmId, userId);
        return rowsAffected > 0;
    }

    @Override
    public Collection<Film> findPopular(Integer limit) {
        log.trace("Find popular initiated");
        return jdbc.query(FIND_POPULAR_QUERY, mapper, limit);
    }

    @Override
    public void addFilmGenresConnection(Long filmId, Set<Long> genreIds) {
        log.trace("Add film genres connection initiated");
        jdbc.batchUpdate(
                INSERT_FILM_GENRE_CONN_QUERY,
                genreIds,
                genreIds.size(),
                (ps, genreId) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, genreId);
                }
        );
    }
}
