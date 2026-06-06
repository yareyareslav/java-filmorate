package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.localDB.GenreDbStorage;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({GenreDbStorage.class, GenreMapper.class})
public class GenreDbStorageTest {
    @Autowired
    private GenreDbStorage genreDbStorage;

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
}
