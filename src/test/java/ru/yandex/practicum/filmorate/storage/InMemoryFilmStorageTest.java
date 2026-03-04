package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryFilmStorageTest {
    private InMemoryFilmStorage inMemoryFilmStorage;

    @BeforeEach
    public void init() {
        inMemoryFilmStorage = new InMemoryFilmStorage();
    }

    @Test
    public void findAll_returnFilmArray() {
        Film film = Film.builder()
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build();

        assertEquals(0, inMemoryFilmStorage.findAll().size());

        inMemoryFilmStorage.create(film);
        inMemoryFilmStorage.create(film);
        inMemoryFilmStorage.create(film);

        assertEquals(3, inMemoryFilmStorage.findAll().size());
    }

    @Test
    public void create_validData_returnFilmWithId() {
        Film film = Film.builder()
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build();

        Film createdFilm = inMemoryFilmStorage.create(film);

        assertTrue(inMemoryFilmStorage.findAll().contains(createdFilm));
        assertEquals(1, createdFilm.getId());
        assertEquals(film.getName(), createdFilm.getName());
        assertEquals(film.getDescription(), createdFilm.getDescription());
        assertEquals(film.getDuration(), createdFilm.getDuration());
        assertEquals(film.getReleaseDate(), createdFilm.getReleaseDate());
    }
}
