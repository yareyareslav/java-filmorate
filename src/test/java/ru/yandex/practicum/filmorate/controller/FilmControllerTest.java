package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.adapters.LocalDateAdapter;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
public class FilmControllerTest {
    final private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService filmService;

    @Test
    void findAll_returnOk() throws Exception {
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void create_validData_returnCreated() throws Exception {
        Film film = Film.builder()
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        String filmJson = gson.toJson(film, Film.class);

        Mockito.when(filmService.create(Mockito.any(Film.class)))
                .thenReturn(Film.builder()
                        .id(1L)
                        .name("Test")
                        .description("Test test")
                        .duration(60L)
                        .releaseDate(LocalDate.of(2012, 12, 12))
                        .build());

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
        Film film = Film.builder()
                .name("")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        String filmJson = gson.toJson(film, Film.class);

        Mockito.when(filmService.create(Mockito.any(Film.class)))
                .thenThrow(ConditionsNotMetException.class);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_validData_returnOk() throws Exception {
        Film film = Film.builder()
                .id(1L)
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        String filmJson = gson.toJson(film, Film.class);

        Mockito.when(filmService.update(Mockito.any(Film.class)))
                .thenReturn(Film.builder()
                        .id(1L)
                        .name("Test")
                        .description("Test test")
                        .duration(60L)
                        .releaseDate(LocalDate.of(2012, 12, 12))
                        .build());

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
        Film film = Film.builder()
                .id(999L)
                .name("Test")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        String filmJson = gson.toJson(film, Film.class);

        Mockito.when(filmService.update(Mockito.any(Film.class)))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_invalidData_badRequest() throws Exception {
        Film film = Film.builder()
                .id(1L)
                .name("")
                .description("Test test")
                .duration(60L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        String filmJson = gson.toJson(film, Film.class);

        Mockito.when(filmService.update(Mockito.any(Film.class)))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isNotFound());
    }
}
