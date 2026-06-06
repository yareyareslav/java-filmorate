package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.OnCreate;
import ru.yandex.practicum.filmorate.dto.film.FilmExtraInfoResponseDto;
import ru.yandex.practicum.filmorate.dto.film.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.film.FilmResponseDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(final FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<FilmExtraInfoResponseDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FilmExtraInfoResponseDto findById(@PathVariable Long id) {
        return filmService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmExtraInfoResponseDto create(@Validated(OnCreate.class) @RequestBody FilmRequestDto dto) {
        return filmService.create(dto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public FilmResponseDto update(@Validated(OnCreate.class) @RequestBody FilmRequestDto dto) {
        return filmService.update(dto);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean addLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean removeLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<FilmResponseDto> getTopByLikes(@RequestParam(defaultValue = "10") int count) {
        return filmService.getTopByLikes(count);
    }
}
