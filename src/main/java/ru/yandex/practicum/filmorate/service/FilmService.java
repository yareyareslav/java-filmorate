package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashMap;

@Service
public class FilmService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
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
        if (film.getDuration().isNegative()) {
            throw new ConditionsNotMetException("Duration must be positive");
        }
    }

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film create(Film film) {
        checkName(film);
        checkDescription(film);
        checkReleaseDate(film);
        checkDuration(film);

        film.setId(getNextId());
        films.put(film.getId(), film);

        return film;
    }

    public Film update(Film film) {
        Long id = film.getId();

        if (id == null) {
            throw new ConditionsNotMetException("Id must not be null");
        }

        Film currentFilm = films.get(id);

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
