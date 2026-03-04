package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Long, Film> films = new HashMap<>();

    public Collection<Film> findAll() {
        log.info("Find all films");
        return films.values();
    }

    public Optional<Film> findOne(Long id) {
        log.info("Find one film initiated");
        Film film = films.get(id);

        if (film == null) {
            return Optional.empty();
        }

        log.info("Film was found. Film id: {}", id);
        return Optional.of(film);
    }

    public Film create(Film film) {
        log.info("Create film initiated");

        film.setId(getNextId());
        film.setLikedUsersIds(new HashSet<>());
        films.put(film.getId(), film);

        log.info("Film created");
        return film;
    }

    public Film update(Film film) {
        Long id = film.getId();
        log.info("Update film initiated. Film Id: {}", id);

        Film currentFilm = films.get(id);

        currentFilm.setName(film.getName());
        currentFilm.setDescription(film.getDescription());
        currentFilm.setDuration(film.getDuration());
        currentFilm.setReleaseDate(film.getReleaseDate());

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
