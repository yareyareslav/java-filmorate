package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Service
public class FilmService {
    private final LocalDate DATE_LIMIT = LocalDate.of(1895, 12, 28);
    private final HashMap<Long, Film> films = new HashMap<>();

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film create(Film film) {
        if (film.getName() == null) {
            throw new ConditionsNotMetException("Name must not be null");
        }
        if (film.getDescription().length() > 200) {
            throw new ConditionsNotMetException("Description must be no longer than 200 symbols");
        }
        if (film.getReleaseDate().isBefore(DATE_LIMIT)) {
            throw new ConditionsNotMetException("Release date must be after 28.12.1895");
        }
        if (film.getDuration().isNegative()) {
            throw new ConditionsNotMetException("Duration must be positive");
        }

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
            currentFilm.setDescription(film.getDescription());
        }
        if (film.getDuration() != null) {
            currentFilm.setDuration(film.getDuration());
        }
        if (film.getReleaseDate() != null) {
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
