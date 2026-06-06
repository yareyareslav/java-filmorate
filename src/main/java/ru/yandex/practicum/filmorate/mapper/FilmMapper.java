package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmExtraInfoResponseDto;
import ru.yandex.practicum.filmorate.dto.film.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.film.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreResponseDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaResponseDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class FilmMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .duration(rs.getLong("duration"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .build();
    }

    public static Film toEntity(FilmRequestDto filmDto) {
        return new Film(
                filmDto.getId(),
                filmDto.getName(),
                filmDto.getDescription(),
                filmDto.getReleaseDate(),
                filmDto.getDuration()
        );
    }

    public static FilmResponseDto toResponse(
            Film film
    ) {
        return new FilmResponseDto(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration()
        );
    }

    public static FilmExtraInfoResponseDto toExtraInfoResponse(
            Film film,
            Set<Long> likedUserIds,
            MpaResponseDto mpa,
            Set<GenreResponseDto> genres) {
        return new FilmExtraInfoResponseDto(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                likedUserIds != null ? likedUserIds : new HashSet<>(),
                mpa,
                genres
        );
    }
}
