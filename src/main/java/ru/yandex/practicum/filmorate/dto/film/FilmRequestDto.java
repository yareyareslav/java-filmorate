package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.dto.OnCreate;
import ru.yandex.practicum.filmorate.dto.OnUpdate;
import ru.yandex.practicum.filmorate.dto.genre.GenreRequestDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaRequestDto;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmRequestDto {
    @NotNull(groups = {OnUpdate.class})
    private Long id;

    @NotBlank(groups = {OnCreate.class})
    private String name;

    @NotBlank(groups = {OnCreate.class})
    @Length(groups = {OnCreate.class, OnUpdate.class}, max = 200)
    private String description;

    @NotNull(groups = {OnCreate.class})
    private LocalDate releaseDate;

    @NotNull(groups = {OnCreate.class})
    @Positive(groups = {OnCreate.class})
    private Long duration;

    private Set<GenreRequestDto> genres;

    private MpaRequestDto mpa;
}
