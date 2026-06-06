package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.FilmMpa;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmMpaMapper implements RowMapper<FilmMpa> {
    @Override
    public FilmMpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new FilmMpa(
                rs.getLong("film_id"),
                rs.getLong("mpa_id")
        );
    }
}
