package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.Optional;

public interface BaseStorage<T> {
    Collection<T> findAll();

    Optional<T> findOne(Long id);

    T create(T film);

    T update(T entity);
}
