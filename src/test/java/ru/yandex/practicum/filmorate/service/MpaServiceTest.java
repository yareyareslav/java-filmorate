package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MpaServiceTest {
    @Mock
    private MpaStorage mpaStorage;

    private MpaService mpaService;

    @BeforeEach
    public void init() {
        mpaService = new MpaService(mpaStorage);
    }

    @Test
    public void findAll_returnAllMpa() {
        Mpa mpa = Mpa.builder().id(1L).name("G").build();
        when(mpaStorage.findAll()).thenReturn(List.of(mpa));

        assertEquals(1, mpaService.findAll().size());
        assertEquals(mpa, mpaService.findAll().iterator().next());
    }

    @Test
    public void findOne_existingMpa_returnMpa() {
        Mpa mpa = Mpa.builder().id(1L).name("G").build();
        when(mpaStorage.findOne(1L)).thenReturn(Optional.of(mpa));

        assertEquals(mpa, mpaService.findOne(1L));
    }

    @Test
    public void findOne_nonExistingMpa_throwNotFoundException() {
        when(mpaStorage.findOne(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> mpaService.findOne(999L));
    }
}
