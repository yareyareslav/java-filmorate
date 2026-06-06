package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.Set;

/**
 * Film.
 */
@Data
@AllArgsConstructor
@Builder
public class Film {
    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private Long duration;
}
