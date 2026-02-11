package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Service
public class FilmService {
    private final LocalDate DATE_LIMIT = LocalDate.of(1895, 12, 28);
    private final HashMap<Long, Film> films = new HashMap<>();

    private void checkName(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ConditionsNotMetException("Name must not be null");
        }
    }

    private void checkDescription(Film film) {
        if (film.getDescription().length() > 200) {
            throw new ConditionsNotMetException("Description must be no longer than 200 symbols");
        }
    }

    private void checkReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(DATE_LIMIT)) {
            throw new ConditionsNotMetException("Release date must be after 28.12.1895");
        }
    }

    private void checkDuration(Film film) {
        if (film.getDuration() < 0) {
            throw new ConditionsNotMetException("Duration must be positive");
        }
    }

    public Collection<Film> findAll() {
        log.info("Find all films");
        return films.values();
    }

    public Film create(Film film) {
        log.info("Create film initiated");

        checkName(film);
        checkDescription(film);
        checkReleaseDate(film);
        checkDuration(film);

        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Film created");
        return film;
    }

    public Film update(Film film) {
        Long id = film.getId();
        log.info("Update film initiated. Film Id: {}", id);

        if (id == null) {
            throw new ConditionsNotMetException("Id must not be null");
        }

        Film currentFilm = films.get(id);

        if (currentFilm == null) {
            throw new NotFoundException("Film not found. Film id: " + id);
        }

        if (film.getName() != null) {
            currentFilm.setName(film.getName());
        }
        if (film.getDescription() != null) {
            checkDescription(film);
            currentFilm.setDescription(film.getDescription());
        }
        if (film.getDuration() != null) {
            checkDuration(film);
            currentFilm.setDuration(film.getDuration());
        }
        if (film.getReleaseDate() != null) {
            checkReleaseDate(film);
            currentFilm.setReleaseDate(film.getReleaseDate());
        }

        films.put(id, currentFilm);

        log.info("Film updated. Id: {}", id);
        return currentFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
