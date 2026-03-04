package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryUserStorageTest {
    private InMemoryUserStorage inMemoryUserStorage;

    @BeforeEach
    public void init() {
        inMemoryUserStorage = new InMemoryUserStorage();
    }

    @Test
    public void findAll_returnUserArray() {
        User user = User.builder()
                .name("Test")
                .email("test@mail.com")
                .login("Test")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        assertEquals(0, inMemoryUserStorage.findAll().size());

        inMemoryUserStorage.create(user);
        inMemoryUserStorage.create(user);
        inMemoryUserStorage.create(user);

        assertEquals(3, inMemoryUserStorage.findAll().size());
    }

    @Test
    public void create_validData_returnUserWithId() {
        User user = User.builder()
                .name("Test")
                .email("test@mail.com")
                .login("Test")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        User createdUser = inMemoryUserStorage.create(user);

        assertTrue(inMemoryUserStorage.findAll().contains(createdUser));
        assertEquals(1, createdUser.getId());
        assertEquals(user.getName(), createdUser.getName());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getLogin(), createdUser.getLogin());
        assertEquals(user.getBirthday(), createdUser.getBirthday());
    }


}
