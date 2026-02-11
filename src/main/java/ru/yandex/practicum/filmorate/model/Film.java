package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@Builder
public class Film {
    public interface CreateFilmInfo {}

    public interface UpdateFilmInfo {}

    @NotNull(groups = UpdateFilmInfo.class)
    private Long id;

    @NotNull(groups = CreateFilmInfo.class)
    @NotBlank(groups = CreateFilmInfo.class)
    private String name;

    @Length(max = 200, groups = { CreateFilmInfo.class, UpdateFilmInfo.class })
    @NotNull(groups = CreateFilmInfo.class)
    @NotBlank(groups = CreateFilmInfo.class)
    private String description;

    @NotNull(groups = CreateFilmInfo.class)
    private LocalDate releaseDate;

    @Min(value = 0, groups = { CreateFilmInfo.class, UpdateFilmInfo.class })
    @NotNull(groups = CreateFilmInfo.class)
    private Long duration;
}
