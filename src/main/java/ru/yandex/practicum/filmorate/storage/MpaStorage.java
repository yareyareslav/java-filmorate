package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Optional;

public interface MpaStorage extends BaseStorage<Mpa> {
    Optional<Mpa> findMpaByFilmId(final long filmId);
}
