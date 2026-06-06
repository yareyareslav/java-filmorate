package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {
    @Mock
    private GenreStorage genreStorage;

    private GenreService genreService;

    @BeforeEach
    public void init() {
        genreService = new GenreService(genreStorage);
    }

    @Test
    public void findAll_returnAllGenres() {
        Genre genre = Genre.builder().id(1L).name("Комедия").build();
        when(genreStorage.findAll()).thenReturn(List.of(genre));

        assertEquals(1, genreService.findAll().size());
        assertEquals(genre, genreService.findAll().iterator().next());
    }

    @Test
    public void findOne_existingGenre_returnGenre() {
        Genre genre = Genre.builder().id(1L).name("Комедия").build();
        when(genreStorage.findOne(1L)).thenReturn(Optional.of(genre));

        assertEquals(genre, genreService.findOne(1L));
    }

    @Test
    public void findOne_nonExistingGenre_throwNotFoundException() {
        when(genreStorage.findOne(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> genreService.findOne(999L));
    }
}
