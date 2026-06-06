package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dto.film.FilmExtraInfoResponseDto;
import ru.yandex.practicum.filmorate.dto.film.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.film.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreRequestDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaRequestDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FilmServiceTest {
    @Mock
    private FilmStorage filmStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private MpaStorage mpaStorage;
    @Mock
    private GenreStorage genreStorage;

    private FilmService filmService;

    @BeforeEach
    public void init() {
        filmService = new FilmService(filmStorage, userStorage, mpaStorage, genreStorage);
    }

    private FilmRequestDto validFilmDto() {
        FilmRequestDto dto = new FilmRequestDto();
        dto.setName("Test");
        dto.setDescription("Test test");
        dto.setDuration(60L);
        dto.setReleaseDate(LocalDate.of(2012, 12, 12));
        return dto;
    }

    private Film filmWithId(long id) {
        return Film.builder()
                .id(id)
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();
    }

    @Test
    public void create_invalidReleaseDate_throwConditionsNotMetException() {
        FilmRequestDto dto = validFilmDto();
        dto.setReleaseDate(LocalDate.of(1800, 12, 12));

        assertThrows(ConditionsNotMetException.class, () -> filmService.create(dto));
        verifyNoInteractions(filmStorage);
    }

    @Test
    public void create_validData_returnCreatedFilm() {
        FilmRequestDto dto = validFilmDto();

        when(filmStorage.create(any(Film.class))).thenAnswer(invocation -> {
            Film film = invocation.getArgument(0);
            film.setId(1L);
            return film;
        });

        FilmExtraInfoResponseDto created = filmService.create(dto);

        assertEquals(1L, created.getId());
        assertEquals(dto.getName(), created.getName());
        assertEquals(dto.getDescription(), created.getDescription());
        assertEquals(dto.getDuration(), created.getDuration());
        assertEquals(dto.getReleaseDate(), created.getReleaseDate());
        verify(filmStorage).create(any(Film.class));
    }

    @Test
    public void create_withMpaAndGenres_returnFilmWithRelations() {
        FilmRequestDto dto = validFilmDto();
        dto.setMpa(new MpaRequestDto(1L));
        dto.setGenres(Set.of(new GenreRequestDto(1L), new GenreRequestDto(2L)));

        when(mpaStorage.findOne(1L)).thenReturn(Optional.of(Mpa.builder().id(1L).name("G").build()));
        when(genreStorage.findAllByIds(Set.of(1L, 2L)))
                .thenReturn(List.of(
                        Genre.builder().id(1L).name("Комедия").build(),
                        Genre.builder().id(2L).name("Драма").build()
                ));
        when(filmStorage.create(any(Film.class))).thenAnswer(invocation -> {
            Film film = invocation.getArgument(0);
            film.setId(1L);
            return film;
        });

        FilmExtraInfoResponseDto created = filmService.create(dto);

        assertNotNull(created.getMpa());
        assertEquals(1L, created.getMpa().getId());
        assertEquals(2, created.getGenres().size());
        verify(filmStorage).addFilmGenresConnection(1L, Set.of(1L, 2L));
        verify(filmStorage).addFilmMpaConnection(1L, 1L);
    }

    @Test
    public void create_invalidGenreId_throwNotFoundException() {
        FilmRequestDto dto = validFilmDto();
        dto.setGenres(Set.of(new GenreRequestDto(1L), new GenreRequestDto(99L)));

        when(genreStorage.findAllByIds(Set.of(1L, 99L)))
                .thenReturn(List.of(Genre.builder().id(1L).name("Комедия").build()));

        assertThrows(NotFoundException.class, () -> filmService.create(dto));
        verify(filmStorage, never()).create(any());
    }

    @Test
    public void update_validData_returnUpdatedFilm() {
        FilmRequestDto dto = new FilmRequestDto();
        dto.setId(1L);
        dto.setName("Updated Test");
        dto.setDescription("Updated Test test");
        dto.setDuration(90L);
        dto.setReleaseDate(LocalDate.of(2000, 12, 12));

        Film existingFilm = filmWithId(1L);
        when(filmStorage.findOne(1L)).thenReturn(Optional.of(existingFilm));
        when(filmStorage.update(any(Film.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FilmResponseDto updatedFilm = filmService.update(dto);

        assertEquals(dto.getId(), updatedFilm.getId());
        assertEquals(dto.getName(), updatedFilm.getName());
        assertEquals(dto.getDescription(), updatedFilm.getDescription());
        assertEquals(dto.getDuration(), updatedFilm.getDuration());
        assertEquals(dto.getReleaseDate(), updatedFilm.getReleaseDate());
    }

    @Test
    public void update_invalidReleaseDate_throwConditionsNotMetException() {
        FilmRequestDto dto = new FilmRequestDto();
        dto.setId(1L);
        dto.setReleaseDate(LocalDate.of(1800, 12, 12));

        when(filmStorage.findOne(1L)).thenReturn(Optional.of(filmWithId(1L)));

        assertThrows(ConditionsNotMetException.class, () -> filmService.update(dto));
        verify(filmStorage, never()).update(any());
    }

    @Test
    public void update_idDoesNotExist_throwNotFoundException() {
        FilmRequestDto dto = validFilmDto();
        dto.setId(999L);

        when(filmStorage.findOne(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.update(dto));
    }

    @Test
    public void findById_existingFilm_returnFilmWithMpaAndGenres() {
        Film film = filmWithId(1L);
        when(filmStorage.findOne(1L)).thenReturn(Optional.of(film));
        when(mpaStorage.findMpaByFilmId(1L)).thenReturn(Optional.of(Mpa.builder().id(1L).name("G").build()));
        when(genreStorage.findAllGenresOfFilmId(1L))
                .thenReturn(List.of(Genre.builder().id(1L).name("Комедия").build()));

        FilmExtraInfoResponseDto result = filmService.findById(1L);

        assertEquals(1L, result.getId());
        assertNotNull(result.getMpa());
        assertEquals(1, result.getGenres().size());
    }

    @Test
    public void findById_filmWithoutMpa_throwNotFoundException() {
        when(filmStorage.findOne(1L)).thenReturn(Optional.of(filmWithId(1L)));
        when(mpaStorage.findMpaByFilmId(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.findById(1L));
    }

    @Test
    public void addLike_hasNoLikeFromTheUser_returnTrue() {
        when(filmStorage.findOne(1L)).thenReturn(Optional.of(filmWithId(1L)));
        when(userStorage.findOne(2L)).thenReturn(Optional.of(User.builder().id(2L).build()));
        when(filmStorage.addLike(1L, 2L)).thenReturn(true);

        assertTrue(filmService.addLike(1L, 2L));
        verify(filmStorage).addLike(1L, 2L);
    }

    @Test
    public void addLike_hasLikeFromTheUser_returnFalse() {
        when(filmStorage.findOne(1L)).thenReturn(Optional.of(filmWithId(1L)));
        when(userStorage.findOne(2L)).thenReturn(Optional.of(User.builder().id(2L).build()));
        when(filmStorage.addLike(1L, 2L)).thenReturn(false);

        assertFalse(filmService.addLike(1L, 2L));
    }

    @Test
    public void addLike_invalidUserId_throwsNotFoundException() {
        when(filmStorage.findOne(1L)).thenReturn(Optional.of(filmWithId(1L)));
        when(userStorage.findOne(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.addLike(1L, 999L));
    }

    @Test
    public void addLike_invalidFilmId_throwsNotFoundException() {
        when(filmStorage.findOne(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.addLike(999L, 2L));
    }

    @Test
    public void removeLike_hasLikeFromTheUser_returnTrue() {
        when(filmStorage.findOne(1L)).thenReturn(Optional.of(filmWithId(1L)));
        when(userStorage.findOne(2L)).thenReturn(Optional.of(User.builder().id(2L).build()));
        when(filmStorage.removeLike(1L, 2L)).thenReturn(true);

        assertTrue(filmService.removeLike(1L, 2L));
    }

    @Test
    public void removeLike_hasNoLikeFromTheUser_returnFalse() {
        when(filmStorage.findOne(1L)).thenReturn(Optional.of(filmWithId(1L)));
        when(userStorage.findOne(2L)).thenReturn(Optional.of(User.builder().id(2L).build()));
        when(filmStorage.removeLike(1L, 2L)).thenReturn(false);

        assertFalse(filmService.removeLike(1L, 2L));
    }

    @Test
    public void removeLike_invalidId_throwsNotFoundException() {
        when(filmStorage.findOne(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.removeLike(999L, 2L));
    }

    @Test
    public void getTopByLikes_returnTopOfCountNumber() {
        Film film1 = filmWithId(1L);
        Film film2 = Film.builder()
                .id(2L)
                .name("Test 2")
                .description("Desc")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        when(filmStorage.findPopular(2)).thenReturn(List.of(film1, film2));

        List<FilmResponseDto> topFilms = filmService.getTopByLikes(2).stream().toList();

        assertEquals(2, topFilms.size());
        assertEquals(1L, topFilms.get(0).getId());
        assertEquals(2L, topFilms.get(1).getId());
    }

    @Test
    public void getTopByLikes_invalidCount_throwConditionsNotMetException() {
        assertThrows(ConditionsNotMetException.class, () -> filmService.getTopByLikes(0));
        assertThrows(ConditionsNotMetException.class, () -> filmService.getTopByLikes(-1));
    }
}
