package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MpaController.class)
@Import(ErrorController.class)
public class MpaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MpaService mpaService;

    @Test
    void findAll_returnOk() throws Exception {
        Mockito.when(mpaService.findAll()).thenReturn(List.of(
                Mpa.builder().id(1L).name("G").build(),
                Mpa.builder().id(2L).name("PG").build()
        ));

        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("G"));
    }

    @Test
    void findOne_returnOk() throws Exception {
        Mockito.when(mpaService.findOne(1L))
                .thenReturn(Mpa.builder().id(1L).name("G").build());

        mockMvc.perform(get("/mpa/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("G"));
    }

    @Test
    void findOne_invalidId_notFound() throws Exception {
        Mockito.when(mpaService.findOne(999L))
                .thenThrow(new NotFoundException("Mpa was not found"));

        mockMvc.perform(get("/mpa/999"))
                .andExpect(status().isNotFound());
    }
}
