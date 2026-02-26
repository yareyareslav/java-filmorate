package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void update_validData_returnUpdatedFilm() {
        Film film = Film.builder()
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        Film createdUser = inMemoryFilmStorage.create(film);

        Film filmToUpdate = Film.builder()
                .id(1L)
                .name("Updated Test")
                .description("Updated Test test")
                .duration(90L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build();

        Film updatedFilm = inMemoryFilmStorage.update(filmToUpdate);

        assertSame(createdUser, updatedFilm, "Films have the same link");
        assertTrue(inMemoryFilmStorage.findAll().contains(updatedFilm));
        assertEquals(1, inMemoryFilmStorage.findAll().size(), "Film list must contain only one object");
        assertEquals(createdUser.getId(), updatedFilm.getId());
        assertEquals(filmToUpdate.getId(), updatedFilm.getId());
        assertEquals(filmToUpdate.getName(), updatedFilm.getName());
        assertEquals(filmToUpdate.getDescription(), updatedFilm.getDescription());
        assertEquals(filmToUpdate.getDuration(), updatedFilm.getDuration());
        assertEquals(filmToUpdate.getReleaseDate(), updatedFilm.getReleaseDate());
    }

    @Test
    public void update_idDoesNotExist_throwNotFoundException() {
        Film film = Film.builder()
                .id(999L)
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        assertThrows(NotFoundException.class, () -> inMemoryFilmStorage.update(film));
    }
}
