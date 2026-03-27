package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage extends BaseStorage<Film> {
    boolean addLike(Long filmId, Long userId);

    boolean removeLike(Long filmId, Long userId);

    Collection<Film> findPopular(Integer limit);
}
