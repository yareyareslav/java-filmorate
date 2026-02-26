package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceTest {
    private FilmService filmService;
    private UserStorage userStorage;
    private FilmStorage filmStorage;

    @BeforeEach
    public void init() {
        userStorage = new InMemoryUserStorage();
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage, userStorage);
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

    @Test
    public void addLike_hasNoLikeFromTheUser_returnTrue() {
        User user = userStorage.create(User.builder()
                .name("Test 1")
                .email("test-1@mail.com")
                .login("Test-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        Film film = Film.builder()
                .id(999L)
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        userStorage.create(user);
        filmStorage.create(film);

        boolean result = filmService.addLike(film.getId(), user.getId());

        assertTrue(result);
        assertTrue(film.getLikedUsersIds().contains(user.getId()));
    }

    @Test
    public void addLike_hasLikeFromTheUser_returnFalse() {
        User user = userStorage.create(User.builder()
                .name("Test 1")
                .email("test-1@mail.com")
                .login("Test-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        Film film = Film.builder()
                .id(999L)
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        userStorage.create(user);
        filmStorage.create(film);

        filmService.addLike(film.getId(), user.getId());
        boolean result = filmService.addLike(film.getId(), user.getId());

        assertFalse(result);
        assertTrue(film.getLikedUsersIds().contains(user.getId()));
    }

    @Test
    public void addLike_invalidId_throwsNotFoundException() {
        User user = userStorage.create(User.builder()
                .name("Test 1")
                .email("test-1@mail.com")
                .login("Test-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        Film film = Film.builder()
                .id(999L)
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        userStorage.create(user);
        filmStorage.create(film);

        assertThrows(NotFoundException.class, () -> filmService.addLike(film.getId(), 999L));
        assertThrows(NotFoundException.class, () -> filmService.addLike(999L, user.getId()));
        assertThrows(NotFoundException.class, () -> filmService.addLike(999L, 999L));
    }

    @Test
    public void removeLike_hasLikeFromTheUser_returnTrue() {
        User user = userStorage.create(User.builder()
                .name("Test 1")
                .email("test-1@mail.com")
                .login("Test-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        Film film = Film.builder()
                .id(999L)
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        userStorage.create(user);
        filmStorage.create(film);
        filmService.addLike(film.getId(), user.getId());

        assertTrue(film.getLikedUsersIds().contains(user.getId()));

        boolean result = filmService.removeLike(film.getId(), user.getId());

        assertTrue(result);
        assertFalse(film.getLikedUsersIds().contains(user.getId()));
    }

    @Test
    public void removeLike_hasNoLikeFromTheUser_returnFalse() {
        User user = userStorage.create(User.builder()
                .name("Test 1")
                .email("test-1@mail.com")
                .login("Test-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        Film film = Film.builder()
                .id(999L)
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        userStorage.create(user);
        filmStorage.create(film);

        assertFalse(film.getLikedUsersIds().contains(user.getId()));

        boolean result = filmService.removeLike(film.getId(), user.getId());

        assertFalse(result);
        assertFalse(film.getLikedUsersIds().contains(user.getId()));
    }

    @Test
    public void removeLike_invalidId_throwsNotFoundException() {
        User user = userStorage.create(User.builder()
                .name("Test 1")
                .email("test-1@mail.com")
                .login("Test-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        Film film = Film.builder()
                .id(999L)
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        userStorage.create(user);
        filmStorage.create(film);

        assertThrows(NotFoundException.class, () -> filmService.removeLike(film.getId(), 999L));
        assertThrows(NotFoundException.class, () -> filmService.removeLike(999L, user.getId()));
        assertThrows(NotFoundException.class, () -> filmService.removeLike(999L, 999L));
    }

    @Test
    public void getTopByLikes_returnTopOfCountNumber() {
        Film testFilm = Film.builder()
                .id(999L)
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();
        Film film1 = filmStorage.create(testFilm);
        Film film2 = filmStorage.create(testFilm);
        Film film3 = filmStorage.create(testFilm);

        User testUser = userStorage.create(User.builder()
                .name("Test 1")
                .email("test-1@mail.com")
                .login("Test-1")
                .birthday(LocalDate.of(2012, 12, 12))
                .build());
        User user1 = userStorage.create(testUser);
        User user2 = userStorage.create(testUser);
        User user3 = userStorage.create(testUser);

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film1.getId(), user3.getId());

        filmService.addLike(film2.getId(), user1.getId());
        filmService.addLike(film2.getId(), user2.getId());

        Collection<Film> topFilms1 = filmService.getTopByLikes(3);
        assertEquals(3, topFilms1.size());
        assertSame(film1, topFilms1.iterator().next());
        assertSame(film2, topFilms1.iterator().next());
        assertSame(film3, topFilms1.iterator().next());

        Collection<Film> topFilms2 = filmService.getTopByLikes(1);
        assertEquals(1, topFilms2.size());
        assertSame(film1, topFilms2.iterator().next());
    }
}
