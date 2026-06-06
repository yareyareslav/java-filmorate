package ru.yandex.practicum.filmorate.dto.mpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MpaResponseDto {
    private Long id;

    private String name;
}
