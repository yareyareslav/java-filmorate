package ru.yandex.practicum.filmorate.dto.genre;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenreResponseDto {
    private Long id;

    private String name;
}
