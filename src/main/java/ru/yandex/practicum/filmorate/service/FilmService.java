package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {
    private final LocalDate dateLimit = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private void checkReleaseDate(Film film) {
        LocalDate release = film.getReleaseDate();
        if (release != null && release.isBefore(dateLimit)) {
            throw new ConditionsNotMetException("Release date must be after 28.12.1895");
        }
    }

    private Film checkFilmExists(final long id) {
        return filmStorage
                .findOne(id)
                .orElseThrow(() -> new NotFoundException("Film is not found. Film id: " + id));
    }

    private User checkUserExists(final long id) {
        return userStorage
                .findOne(id)
                .orElseThrow(() -> new NotFoundException("User is not found. User id: " + id));
    }

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(final Film film) {
        checkReleaseDate(film);
        return filmStorage.create(film);
    }

    public Film update(final Film film) {
        Film currentFilm = checkFilmExists(film.getId());
        checkReleaseDate(film);

        String name = film.getName();
        String description = film.getDescription();
        Long duration = film.getDuration();
        LocalDate releaseDate = film.getReleaseDate();

        if (name != null && !name.isBlank()) {
            currentFilm.setName(name);
        }
        if (description != null && !description.isBlank()) {
            currentFilm.setDescription(description);
        }
        if (duration != null) {
            currentFilm.setDuration(duration);
        }
        if (releaseDate != null) {
            currentFilm.setReleaseDate(releaseDate);
        }

        return filmStorage
                .update(currentFilm)
                .orElseThrow(() -> new NotFoundException("Film is not found. Film id: " + film.getId()));
    }

    public boolean addLike(Long id, Long userId) {
        log.info("Add like initiated. Film id: {}, User id: {}", id, userId);

        Film film = checkFilmExists(id);
        checkUserExists(userId);

        log.info("Add like ended. Film id: {}, User id: {}", id, userId);
        return film.getLikedUsersIds().add(userId);
    }

    public boolean removeLike(Long id, Long userId) {
        log.info("Remove like initiated. Film id: {}, User id: {}", id, userId);

        Film film = checkFilmExists(id);
        checkUserExists(userId);

        log.info("Remove like ended. Film id: {}, User id: {}", id, userId);
        return film.getLikedUsersIds().remove(userId);
    }

    public Collection<Film> getTopByLikes(int count) {
        log.info("Get top by likes initiated. Count: {}", count);

        if (count <= 0) {
            throw new ConditionsNotMetException("Count must be positive");
        }

        return filmStorage.findAll()
                .stream()
                .sorted(Comparator.comparingInt(f -> ((Film) f).getLikedUsersIds().size()).reversed())
                .limit(count)
                .toList();
    }
}
