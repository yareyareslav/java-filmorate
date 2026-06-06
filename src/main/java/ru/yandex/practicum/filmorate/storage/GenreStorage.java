package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage extends BaseStorage<Genre> {
    List<Genre> findAllByIds(Set<Long> ids);

    List<Genre> findAllGenresOfFilmId(long filmId);
}
