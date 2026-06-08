package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreController.class)
@Import(ErrorController.class)
public class GenreControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService genreService;

    @Test
    void findAll_returnOk() throws Exception {
        Mockito.when(genreService.findAll()).thenReturn(List.of(
                Genre.builder().id(1L).name("Комедия").build(),
                Genre.builder().id(2L).name("Драма").build()
        ));

        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Комедия"))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void findOne_returnOk() throws Exception {
        Mockito.when(genreService.findOne(1L))
                .thenReturn(Genre.builder().id(1L).name("Комедия").build());

        mockMvc.perform(get("/genres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Комедия"));
    }

    @Test
    void findOne_invalidId_notFound() throws Exception {
        Mockito.when(genreService.findOne(999L))
                .thenThrow(new NotFoundException("Genre was not found"));

        mockMvc.perform(get("/genres/999"))
                .andExpect(status().isNotFound());
    }
}
