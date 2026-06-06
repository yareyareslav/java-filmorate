package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.genre.GenreResponseDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreMapper implements RowMapper<Genre> {
    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Genre
                .builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }

    public static GenreResponseDto toResponse(Genre genre) {
        return new GenreResponseDto(
                genre.getId(),
                genre.getName())
                ;
    }
}
