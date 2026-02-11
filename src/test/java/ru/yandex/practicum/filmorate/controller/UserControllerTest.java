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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    final private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void findAll_returnOk() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void create_validData_returnCreated() throws Exception {
        User user = User.builder()
                .name("Test")
                .login("Test Login")
                .email("test@mail.ru")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        String userJson = gson.toJson(user, User.class);

        Mockito.when(userService.create(Mockito.any(User.class)))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("Test")
                        .login("Test Login")
                        .email("test@mail.ru")
                        .birthday(LocalDate.of(2012, 12, 12))
                        .build());

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.login").value("Test Login"))
                .andExpect(jsonPath("$.email").value("test@mail.ru"))
                .andExpect(jsonPath("$.birthday").value("2012-12-12"));
    }

    @Test
    void create_invalidData_returnBadRequest() throws Exception {
        User user = User.builder()
                .name("Test")
                .email("test@mail.ru")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        String userJson = gson.toJson(user, User.class);

        Mockito.when(userService.create(Mockito.any(User.class)))
                        .thenThrow(ConditionsNotMetException.class);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_validData_returnOk() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("Test")
                .login("Test Login")
                .email("test@mail.ru")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        String userJson = gson.toJson(user, User.class);

        Mockito.when(userService.update(Mockito.any(User.class)))
                .thenReturn(user);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.login").value("Test Login"))
                .andExpect(jsonPath("$.email").value("test@mail.ru"))
                .andExpect(jsonPath("$.birthday").value("2012-12-12"));
    }

    @Test
    void update_invalidId_notFound() throws Exception {
        User user = User.builder()
                .id(999L)
                .name("Test")
                .email("test@mail.ru")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        String userJson = gson.toJson(user, User.class);

        Mockito.when(userService.update(Mockito.any(User.class)))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_invalidData_badRequest() throws Exception {
        User user = User.builder()
                .id(999L)
                .name("Test")
                .email("invalid-mail.ru")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        String userJson = gson.toJson(user, User.class);

        Mockito.when(userService.update(Mockito.any(User.class)))
                .thenThrow(ConditionsNotMetException.class);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
    }
}
