package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.localDB.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.localDB.GenreDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({GenreDbStorage.class, GenreMapper.class, FilmDbStorage.class, FilmMapper.class})
public class GenreDbStorageTest {
    @Autowired
    private GenreDbStorage genreDbStorage;

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Test
    public void findAll_returnAllGenresFromDataSql() {
        assertEquals(3, genreDbStorage.findAll().size());
    }

    @Test
    public void findOne_existingGenre_returnGenre() {
        Genre genre = genreDbStorage.findOne(1L).orElseThrow();

        assertEquals(1L, genre.getId());
        assertEquals("Комедия", genre.getName());
    }

    @Test
    public void findOne_nonExistingGenre_returnEmpty() {
        assertTrue(genreDbStorage.findOne(999L).isEmpty());
    }

    @Test
    public void findAllByIds_returnMatchingGenres() {
        List<Genre> genres = genreDbStorage.findAllByIds(Set.of(1L, 2L));

        assertEquals(2, genres.size());
    }

    @Test
    public void findGenresByFilmIds_returnGenresGroupedByFilmId() {
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

        filmDbStorage.addFilmGenresConnection(film1.getId(), Set.of(1L, 2L));
        filmDbStorage.addFilmGenresConnection(film2.getId(), Set.of(2L, 3L));

        Map<Long, Set<Genre>> genresByFilmId = genreDbStorage.findGenresByFilmIds(
                List.of(film1.getId(), film2.getId()));

        assertEquals(2, genresByFilmId.get(film1.getId()).size());
        assertEquals(2, genresByFilmId.get(film2.getId()).size());
        assertTrue(genresByFilmId.get(film1.getId()).stream().anyMatch(g -> g.getId().equals(1L)));
        assertTrue(genresByFilmId.get(film2.getId()).stream().anyMatch(g -> g.getId().equals(3L)));
    }

    @Test
    public void findGenresByFilmIds_emptyCollection_returnEmptyMap() {
        assertTrue(genreDbStorage.findGenresByFilmIds(List.of()).isEmpty());
    }
}
