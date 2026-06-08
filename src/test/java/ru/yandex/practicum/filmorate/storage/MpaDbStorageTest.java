package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.localDB.MpaDbStorage;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({MpaDbStorage.class, MpaMapper.class})
public class MpaDbStorageTest {
    @Autowired
    private MpaDbStorage mpaDbStorage;

    @Test
    public void findAll_returnAllMpaFromDataSql() {
        assertEquals(3, mpaDbStorage.findAll().size());
    }

    @Test
    public void findOne_existingMpa_returnMpa() {
        Mpa mpa = mpaDbStorage.findOne(1L).orElseThrow();

        assertEquals(1L, mpa.getId());
        assertEquals("G", mpa.getName());
    }

    @Test
    public void findOne_nonExistingMpa_returnEmpty() {
        assertTrue(mpaDbStorage.findOne(999L).isEmpty());
    }
}
