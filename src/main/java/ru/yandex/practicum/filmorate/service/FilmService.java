package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Service
public class FilmService {
    private final LocalDate dateLimit = LocalDate.of(1895, 12, 28);
    private final HashMap<Long, Film> films = new HashMap<>();

    private void checkReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(dateLimit)) {
            throw new ConditionsNotMetException("Release date must be after 28.12.1895");
        }
    }

    public Collection<Film> findAll() {
        log.info("Find all films");
        return films.values();
    }

    public Film create(Film film) {
        log.info("Create film initiated");

        checkReleaseDate(film);

        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Film created");
        return film;
    }

    public Film update(Film film) {
        Long id = film.getId();
        log.info("Update film initiated. Film Id: {}", id);

        Film currentFilm = films.get(id);

        if (currentFilm == null) {
            throw new NotFoundException("Film not found. Film id: " + id);
        }

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
            checkReleaseDate(film);
            currentFilm.setReleaseDate(releaseDate);
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
