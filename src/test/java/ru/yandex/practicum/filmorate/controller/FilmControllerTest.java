package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.adapters.LocalDateAdapter;
import ru.yandex.practicum.filmorate.dto.film.FilmExtraInfoResponseDto;
import ru.yandex.practicum.filmorate.dto.film.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.film.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaRequestDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
@Import(ErrorController.class)
public class FilmControllerTest {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService filmService;

    private FilmRequestDto validFilmDto() {
        FilmRequestDto dto = new FilmRequestDto();
        dto.setName("Test");
        dto.setDescription("Test test");
        dto.setDuration(60L);
        dto.setReleaseDate(LocalDate.of(2012, 12, 12));
        dto.setMpa(new MpaRequestDto(1L));
        return dto;
    }

    private FilmExtraInfoResponseDto filmExtraInfoResponse() {
        FilmExtraInfoResponseDto dto = new FilmExtraInfoResponseDto();
        dto.setId(1L);
        dto.setName("Test");
        dto.setDescription("Test test");
        dto.setDuration(60L);
        dto.setReleaseDate(LocalDate.of(2012, 12, 12));
        return dto;
    }

    private FilmResponseDto filmResponse() {
        return new FilmResponseDto(1L, "Test", "Test test",
                LocalDate.of(2012, 12, 12), 60L);
    }

    @Test
    void findAll_returnOk() throws Exception {
        Mockito.when(filmService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void findById_returnOk() throws Exception {
        Mockito.when(filmService.findById(1L)).thenReturn(filmExtraInfoResponse());

        mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    void create_validData_returnCreated() throws Exception {
        FilmRequestDto filmDto = validFilmDto();
        String filmJson = gson.toJson(filmDto, FilmRequestDto.class);

        Mockito.when(filmService.create(any(FilmRequestDto.class)))
                .thenReturn(filmExtraInfoResponse());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.description").value("Test test"))
                .andExpect(jsonPath("$.duration").value(60))
                .andExpect(jsonPath("$.releaseDate").value("2012-12-12"));
    }

    @Test
    void create_invalidData_returnBadRequest() throws Exception {
        FilmRequestDto filmDto = validFilmDto();
        filmDto.setName("");
        String filmJson = gson.toJson(filmDto, FilmRequestDto.class);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_invalidReleaseDate_returnBadRequest() throws Exception {
        FilmRequestDto filmDto = validFilmDto();
        String filmJson = gson.toJson(filmDto, FilmRequestDto.class);

        Mockito.when(filmService.create(any(FilmRequestDto.class)))
                .thenThrow(new ConditionsNotMetException("Release date must be after 28.12.1895"));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_validData_returnOk() throws Exception {
        FilmRequestDto filmDto = validFilmDto();
        filmDto.setId(1L);
        String filmJson = gson.toJson(filmDto, FilmRequestDto.class);

        Mockito.when(filmService.update(any(FilmRequestDto.class)))
                .thenReturn(filmResponse());

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.description").value("Test test"))
                .andExpect(jsonPath("$.duration").value(60))
                .andExpect(jsonPath("$.releaseDate").value("2012-12-12"));
    }

    @Test
    void update_invalidId_notFound() throws Exception {
        FilmRequestDto filmDto = validFilmDto();
        filmDto.setId(999L);
        String filmJson = gson.toJson(filmDto, FilmRequestDto.class);

        Mockito.when(filmService.update(any(FilmRequestDto.class)))
                .thenThrow(new NotFoundException("Film is not found"));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void addLike_returnOk() throws Exception {
        Mockito.when(filmService.addLike(1L, 2L)).thenReturn(true);

        mockMvc.perform(put("/films/1/like/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void removeLike_returnOk() throws Exception {
        Mockito.when(filmService.removeLike(1L, 2L)).thenReturn(true);

        mockMvc.perform(delete("/films/1/like/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void getTopByLikes_returnOk() throws Exception {
        FilmExtraInfoResponseDto film = filmExtraInfoResponse();
        film.setMpa(new ru.yandex.practicum.filmorate.dto.mpa.MpaResponseDto(1L, "G"));

        Mockito.when(filmService.getTopByLikes(anyInt()))
                .thenReturn(List.of(film));

        mockMvc.perform(get("/films/popular").param("count", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test"))
                .andExpect(jsonPath("$[0].mpa.id").value(1))
                .andExpect(jsonPath("$[0].mpa.name").value("G"));
    }
}
