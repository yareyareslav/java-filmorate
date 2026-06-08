package ru.yandex.practicum.filmorate.dto.film;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.genre.GenreResponseDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaResponseDto;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmExtraInfoResponseDto {
    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private Long duration;

    private Set<Long> likedUserIds;

    private MpaResponseDto mpa;

    private Set<GenreResponseDto> genres;
}
