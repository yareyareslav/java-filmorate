package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmExtraInfoResponseDto;
import ru.yandex.practicum.filmorate.dto.film.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.film.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreRequestDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreResponseDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaResponseDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final LocalDate dateLimit = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("mpaDbStorage") MpaStorage mpaStorage,
            @Qualifier("genreDbStorage") GenreStorage genreStorage
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    private void checkReleaseDate(LocalDate release) {
        if (release != null && release.isBefore(dateLimit)) {
            log.warn("Release date {} is before 28.12.1985", release);
            throw new ConditionsNotMetException("Release date must be after 28.12.1895");
        }
    }

    private Film checkFilmExists(final long id) {
        return filmStorage
                .findOne(id)
                .orElseThrow(() -> {
                    log.warn("Film not found. Id: {}", id);
                    return new NotFoundException("Film is not found. Film id: " + id);
                });
    }

    private User checkUserExists(final long id) {
        return userStorage
                .findOne(id)
                .orElseThrow(() -> {
                    log.warn("User not found. Id: {}", id);
                    return new NotFoundException("User is not found. User id: " + id);
                });
    }

    private Mpa checkMpaExists(final long id) {
        return mpaStorage
                .findOne(id)
                .orElseThrow(() -> {
                    log.warn("Mpa not found. Id: {}", id);
                    return new NotFoundException("Mpa is not found. Mpa id: " + id);
                });
    }

    private List<Genre> checkGenresExists(final Set<Long> ids) {
        return genreStorage
                .findAllByIds(ids);
    }

    public Collection<FilmExtraInfoResponseDto> findAll() {
        return filmStorage.findAll().stream()
                .map(f -> FilmMapper.toExtraInfoResponse(f, null, null, null))
                .toList();
    }

    public FilmExtraInfoResponseDto findById(final long id) {
        Film film = checkFilmExists(id);
        MpaResponseDto mpaDto = MpaMapper.toResponse(
                mpaStorage
                        .findMpaByFilmId(id)
                        .orElseThrow(() ->
                                new NotFoundException("Mpa of film not found. Film id: " + id)));
        TreeSet<GenreResponseDto> genreDtos = new HashSet<>(genreStorage.findAllGenresOfFilmId(id))
                .stream()
                .map(GenreMapper::toResponse)
                .collect(Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparing(GenreResponseDto::getId))));

        return FilmMapper.toExtraInfoResponse(film, null, mpaDto, genreDtos);
    }

    public FilmExtraInfoResponseDto create(final FilmRequestDto filmDto) {
        checkReleaseDate(filmDto.getReleaseDate());

        Set<Long> existingGenreIds = null;
        TreeSet<GenreResponseDto> genreResponseDtos = null;

        log.info("FILM DTO: {}", filmDto);

        if (filmDto.getGenres() != null) {
            Set<Long> genreIds = filmDto.getGenres().stream()
                    .map(GenreRequestDto::getId).collect(Collectors.toSet());
            List<Genre> existingGenres = checkGenresExists(genreIds);
            existingGenreIds = existingGenres.stream()
                    .map(Genre::getId).collect(Collectors.toSet());
            if (genreIds.size() != existingGenreIds.size()) {
                Set<Long> diff = new HashSet<>(genreIds);
                diff.removeAll(existingGenreIds);
                throw new NotFoundException("These genres are not found: " + diff);
            }
            genreResponseDtos = existingGenres.stream()
                    .map(GenreMapper::toResponse)
                        .collect(Collectors.toCollection(
                                () -> new TreeSet<>(Comparator.comparing(GenreResponseDto::getId))
                        ));
        }

        Mpa mpa = null;
        if (filmDto.getMpa() != null) {
            mpa = checkMpaExists(filmDto.getMpa().getId());
        }

        Film createdFilm = filmStorage.create(FilmMapper.toEntity(filmDto));

        if (existingGenreIds != null && !existingGenreIds.isEmpty()) {
            filmStorage.addFilmGenresConnection(createdFilm.getId(), existingGenreIds);
        }
        if (mpa != null && mpa.getId() != null) {
            filmStorage.addFilmMpaConnection(createdFilm.getId(), mpa.getId());
        }

        return FilmMapper.toExtraInfoResponse(
                createdFilm,
                null,
                MpaMapper.toResponse(mpa),
                genreResponseDtos
        );
    }

    public FilmResponseDto update(final FilmRequestDto filmDto) {
        Film currentFilm = checkFilmExists(filmDto.getId());

        String name = filmDto.getName();
        String description = filmDto.getDescription();
        Long duration = filmDto.getDuration();
        LocalDate releaseDate = filmDto.getReleaseDate();

        if (name != null && !name.isBlank()) {
            currentFilm.setName(name);
        }
        if (description != null && !description.isBlank()) {
            currentFilm.setDescription(description);
        }
        if (duration != null) {
            currentFilm.setDuration(duration);
        }
        if (releaseDate != null) {
            checkReleaseDate(filmDto.getReleaseDate());
            currentFilm.setReleaseDate(releaseDate);
        }

        return FilmMapper.toResponse(filmStorage.update(currentFilm));
    }

    public boolean addLike(Long id, Long userId) {
        log.info("Add like initiated. Film id: {}, User id: {}", id, userId);

        checkFilmExists(id);
        checkUserExists(userId);

        log.info("Add like ended. Film id: {}, User id: {}", id, userId);
        return filmStorage.addLike(id, userId);
    }

    public boolean removeLike(Long id, Long userId) {
        log.info("Remove like initiated. Film id: {}, User id: {}", id, userId);

        checkFilmExists(id);
        checkUserExists(userId);

        log.info("Remove like ended. Film id: {}, User id: {}", id, userId);
        return filmStorage.removeLike(id, userId);
    }

    public Collection<FilmResponseDto> getTopByLikes(int count) {
        log.info("Get top by likes initiated. Count: {}", count);

        if (count <= 0) {
            throw new ConditionsNotMetException("Count must be positive");
        }

        return filmStorage.findPopular(count).stream()
                .map(FilmMapper::toResponse)
                .toList();
    }
}
