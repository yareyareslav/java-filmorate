package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAll();

    Optional<User> findOne(Long id);

    User create(User user);

    Optional<User> update(User user);
}
