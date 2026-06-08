package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GenreStorage extends BaseStorage<Genre> {
    List<Genre> findAllByIds(Set<Long> ids);

    Map<Long, Set<Genre>> findGenresByFilmIds(Collection<Long> filmIds);
}
