package ru.yandex.practicum.filmorate.dto.genre;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.OnCreate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreRequestDto {
    @NotNull(groups = {OnCreate.class})
    private Long id;
}
