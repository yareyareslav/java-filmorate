package ru.yandex.practicum.filmorate.dto.mpa;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.OnCreate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MpaRequestDto {
    @NotNull(groups = {OnCreate.class})
    private Long id;
}
