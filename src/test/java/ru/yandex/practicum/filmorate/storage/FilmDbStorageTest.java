package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.localDB.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.localDB.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({FilmDbStorage.class, FilmMapper.class, UserDbStorage.class, UserMapper.class})
public class FilmDbStorageTest {
    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private UserDbStorage userDbStorage;

    @Test
    public void findAll_returnFilmArray() {
        Film film = Film.builder()
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build();

        assertEquals(0, filmDbStorage.findAll().size());

        filmDbStorage.create(film);
        filmDbStorage.create(film);
        filmDbStorage.create(film);

        assertEquals(3, filmDbStorage.findAll().size());
    }

    @Test
    public void create_validData_returnFilmWithId() {
        Film film = Film.builder()
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build();

        Film createdFilm = filmDbStorage.create(film);

        assertTrue(filmDbStorage.findAll().contains(createdFilm));
        assertNotNull(createdFilm.getId());
        assertEquals(film.getName(), createdFilm.getName());
        assertEquals(film.getDescription(), createdFilm.getDescription());
        assertEquals(film.getDuration(), createdFilm.getDuration());
        assertEquals(film.getReleaseDate(), createdFilm.getReleaseDate());
    }

    @Test
    public void findOne_existingFilm_returnFilm() {
        Film createdFilm = filmDbStorage.create(Film.builder()
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build());

        Film foundFilm = filmDbStorage.findOne(createdFilm.getId()).orElseThrow();

        assertEquals(createdFilm.getId(), foundFilm.getId());
        assertEquals(createdFilm.getName(), foundFilm.getName());
    }

    @Test
    public void addLike_andRemoveLike_workCorrectly() {
        User user = userDbStorage.create(User.builder()
                .name("User")
                .email("like-user@mail.com")
                .login("like_user")
                .birthday(LocalDate.of(1990, 1, 1))
                .build());
        Film film = filmDbStorage.create(Film.builder()
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build());

        assertTrue(filmDbStorage.addLike(film.getId(), user.getId()));
        assertThrows(DuplicateKeyException.class,
                () -> filmDbStorage.addLike(film.getId(), user.getId()));
        assertTrue(filmDbStorage.removeLike(film.getId(), user.getId()));
        assertFalse(filmDbStorage.removeLike(film.getId(), user.getId()));
    }

    @Test
    public void create_withMpa_persistsMpaReference() {
        Film createdFilm = filmDbStorage.create(Film.builder()
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .mpa(Mpa.builder().id(1L).name("G").build())
                .build());

        Film foundFilm = filmDbStorage.findOne(createdFilm.getId()).orElseThrow();

        assertNotNull(foundFilm.getMpa());
        assertEquals(1L, foundFilm.getMpa().getId());
    }

    @Test
    public void addFilmGenresConnection_persistRelations() {
        Film film = filmDbStorage.create(Film.builder()
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .mpa(Mpa.builder().id(1L).name("G").build())
                .build());

        assertDoesNotThrow(() ->
                filmDbStorage.addFilmGenresConnection(film.getId(), Set.of(1L, 2L)));
    }

    @Test
    public void findPopular_returnFilmsOrderedByLikes() {
        User user1 = userDbStorage.create(User.builder()
                .name("User 1")
                .email("popular-user1@mail.com")
                .login("popular_user1")
                .birthday(LocalDate.of(1990, 1, 1))
                .build());
        User user2 = userDbStorage.create(User.builder()
                .name("User 2")
                .email("popular-user2@mail.com")
                .login("popular_user2")
                .birthday(LocalDate.of(1991, 1, 1))
                .build());
        Mpa mpa = Mpa.builder().id(1L).name("G").build();
        Film film1 = filmDbStorage.create(Film.builder()
                .name("Film 1")
                .description("Desc")
                .duration(60L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .mpa(mpa)
                .build());
        Film film2 = filmDbStorage.create(Film.builder()
                .name("Film 2")
                .description("Desc")
                .duration(60L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .mpa(mpa)
                .build());

        filmDbStorage.addLike(film1.getId(), user1.getId());
        filmDbStorage.addLike(film1.getId(), user2.getId());
        filmDbStorage.addLike(film2.getId(), user1.getId());

        Collection<Film> popular = filmDbStorage.findPopular(2);

        assertEquals(2, popular.size());
        assertEquals(film1.getId(), popular.iterator().next().getId());
    }
}
