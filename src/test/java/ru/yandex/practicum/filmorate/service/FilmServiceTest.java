package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmServiceTest {
    private FilmService filmService;

    @BeforeEach
    public void init() {
        filmService = new FilmService();
    }

    @Test
    public void findAll_returnFilmArray() {
        Film film = Film.builder()
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build();

        assertEquals(0, filmService.findAll().size());

        filmService.create(film);
        filmService.create(film);
        filmService.create(film);

        assertEquals(3, filmService.findAll().size());
    }

    @Test
    public void create_validData_returnFilmWithId() {
        Film film = Film.builder()
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build();

        Film createdFilm = filmService.create(film);

        assertTrue(filmService.findAll().contains(createdFilm));
        assertEquals(1, createdFilm.getId());
        assertEquals(film.getName(), createdFilm.getName());
        assertEquals(film.getDescription(), createdFilm.getDescription());
        assertEquals(film.getDuration(), createdFilm.getDuration());
        assertEquals(film.getReleaseDate(), createdFilm.getReleaseDate());
    }

    @Test
    public void create_invalidName_throwConditionsNotMetException() {
        Film filmNameNull = Film.builder()
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build();
        Film filmNameBlank = Film.builder()
                .name("")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> filmService.create(filmNameNull));
        assertThrows(ConditionsNotMetException.class, () -> filmService.create(filmNameBlank));
    }

    @Test
    public void create_invalidDescription_throwConditionsNotMetException() {
        Film filmDescriptionMoreThan200Char = Film.builder()
                .name("Test")
                .description("s".repeat(201))
                .duration(60L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> filmService.create(filmDescriptionMoreThan200Char));
    }

    @Test
    public void create_invalidReleaseDate_throwConditionsNotMetException() {
        Film filmEarlierThan1985 = Film.builder()
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(1800, 12, 12))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> filmService.create(filmEarlierThan1985));
    }

    @Test
    public void create_invalidDuration_throwConditionsNotMetException() {
        Film filmNegativeDuration = Film.builder()
                .name("Test")
                .description("Test test")
                .duration(-60L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> filmService.create(filmNegativeDuration));
    }

    @Test
    public void update_validData_returnUpdatedFilm() {
        Film film = Film.builder()
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        Film createdUser = filmService.create(film);

        Film filmToUpdate = Film.builder()
                .id(1L)
                .name("Updated Test")
                .description("Updated Test test")
                .duration(90L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build();

        Film updatedFilm = filmService.update(filmToUpdate);

        assertSame(createdUser, updatedFilm, "Films have the same link");
        assertTrue(filmService.findAll().contains(updatedFilm));
        assertEquals(1, filmService.findAll().size(), "Film list must contain only one object");
        assertEquals(createdUser.getId(), updatedFilm.getId());
        assertEquals(filmToUpdate.getId(), updatedFilm.getId());
        assertEquals(filmToUpdate.getName(), updatedFilm.getName());
        assertEquals(filmToUpdate.getDescription(), updatedFilm.getDescription());
        assertEquals(filmToUpdate.getDuration(), updatedFilm.getDuration());
        assertEquals(filmToUpdate.getReleaseDate(), updatedFilm.getReleaseDate());
    }

    @Test
    public void update_idNull_throwConditionsNotMetException() {
        Film film = Film.builder()
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> filmService.update(film));
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

        assertThrows(NotFoundException.class, () -> filmService.update(film));
    }

    @Test
    public void update_invalidDescription_throwConditionsNotMetException() {
        Film film = Film.builder()
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        filmService.create(film);

        Film filmDescriptionMoreThan200Char = Film.builder()
                .id(1L)
                .description("s".repeat(201))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> filmService.update(filmDescriptionMoreThan200Char));
    }

    @Test
    public void update_invalidDuration_throwConditionsNotMetException() {
        Film film = Film.builder()
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        filmService.create(film);

        Film filmDurationNegative = Film.builder()
                .id(1L)
                .duration(-60L)
                .build();

        assertThrows(ConditionsNotMetException.class, () -> filmService.update(filmDurationNegative));
    }

    @Test
    public void update_invalidReleaseDate_throwConditionsNotMetException() {
        Film film = Film.builder()
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        filmService.create(film);

        Film filmReleaseDateBefore1895 = Film.builder()
                .id(1L)
                .releaseDate(LocalDate.of(1800, 12, 12))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> filmService.update(filmReleaseDateBefore1895));
    }

}
