package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(final Film film) {
        return filmStorage.create(film);
    }

    public Film update(final Film film) {
        return filmStorage.update(film);
    }

    public boolean addLike(Long id, Long userId) {
        log.info("Add like initiated. Film id: {}, User id: {}", id, userId);

        Film film = filmStorage.findOne(id);
        if (filmStorage.findOne(id) == null) {
            throw new NotFoundException("Film is not found. Film id: " + id);
        }

        User user = userStorage.findOne(userId);
        if (user == null) {
            throw new NotFoundException("User is not found. User id: " + userId);
        }

        log.info("Add like ended. Film id: {}, User id: {}", id, userId);
        return film.getLikedUsersIds().add(userId);
    }

    public boolean removeLike(Long id, Long userId) {
        log.info("Remove like initiated. Film id: {}, User id: {}", id, userId);

        Film film = filmStorage.findOne(id);
        if (filmStorage.findOne(id) == null) {
            throw new NotFoundException("Film is not found. Film id: " + id);
        }

        User user = userStorage.findOne(userId);
        if (user == null) {
            throw new NotFoundException("User is not found. User id: " + userId);
        }

        log.info("Remove like ended. Film id: {}, User id: {}", id, userId);
        return film.getLikedUsersIds().remove(userId);
    }

    public Collection<Film> getTopByLikes(int count) {
        log.info("Get top by likes initiated. Count: {}", count);
        return filmStorage.findAll()
                .stream()
                .sorted(Comparator.comparingInt(f -> ((Film) f).getLikedUsersIds().size()).reversed())
                .limit(count)
                .toList();
    }
}
