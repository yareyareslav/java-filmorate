package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public boolean addLike(Long id, Long userId) {
        Film film = filmStorage.findOne(id);
        if (filmStorage.findOne(id) == null) {
            throw new NotFoundException("Film is not found. Film id: " + id);
        }

        User user = userStorage.findOne(userId);
        if (user == null) {
            throw new NotFoundException("User is not found. User id: " + userId);
        }

        return film.getLikedUsersIds().add(userId);
    }

    public boolean removeLike(Long id, Long userId) {
        Film film = filmStorage.findOne(id);
        if (filmStorage.findOne(id) == null) {
            throw new NotFoundException("Film is not found. Film id: " + id);
        }

        User user = userStorage.findOne(userId);
        if (user == null) {
            throw new NotFoundException("User is not found. User id: " + userId);
        }

        return film.getLikedUsersIds().remove(userId);
    }

    public Collection<Film> getTopByLikes(int count) {
        return filmStorage.findAll()
                .stream()
                .sorted(Comparator.comparingInt(f -> f.getLikedUsersIds().size()))
                .limit(count)
                .toList();
    }
}
