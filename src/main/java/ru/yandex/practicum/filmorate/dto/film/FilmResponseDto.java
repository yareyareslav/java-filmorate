package ru.yandex.practicum.filmorate.dto.film;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmResponseDto {
    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private Long duration;
}
