package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

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

    @Test
    public void create_nameIsNull_returnUserWithIdAndName() {
        User user = User.builder()
                .email("test@mail.com")
                .login("Test")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        User createdUser = inMemoryUserStorage.create(user);

        assertTrue(inMemoryUserStorage.findAll().contains(createdUser));
        assertEquals(1, createdUser.getId());
        assertEquals(user.getLogin(), createdUser.getName(), "Name must be equal to login");
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getLogin(), createdUser.getLogin());
        assertEquals(user.getBirthday(), createdUser.getBirthday());
    }

    @Test
    public void create_invalidLogin_throwConditionsNotMetException() {
        User userLoginWhitespace = User.builder()
                .name("Test")
                .email("test@mail.com")
                .login("Login with whitespace")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> inMemoryUserStorage.create(userLoginWhitespace));
    }

    @Test
    public void update_validData_returnUpdatedUser() {
        User user = User.builder()
                .name("Test")
                .email("test@mail.com")
                .login("Test")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        User createdUser = inMemoryUserStorage.create(user);

        User userToUpdate = User.builder()
                .id(1L)
                .name("Test Update")
                .email("update-test@mail.com")
                .login("Test-Update-Login")
                .birthday(LocalDate.of(2000, 12, 12))
                .build();

        User updatedUser = inMemoryUserStorage.update(userToUpdate);

        assertSame(createdUser, updatedUser, "Users have the same link");
        assertTrue(inMemoryUserStorage.findAll().contains(updatedUser));
        assertEquals(1, inMemoryUserStorage.findAll().size(), "User list must contain only one object");
        assertEquals(createdUser.getId(), updatedUser.getId());
        assertEquals(userToUpdate.getId(), updatedUser.getId());
        assertEquals(userToUpdate.getName(), updatedUser.getName());
        assertEquals(userToUpdate.getLogin(), updatedUser.getLogin());
        assertEquals(userToUpdate.getEmail(), updatedUser.getEmail());
        assertEquals(userToUpdate.getBirthday(), updatedUser.getBirthday());
    }

    @Test
    public void update_idDoesNotExist_throwNotFoundException() {
        User user = User.builder()
                .id(999L)
                .name("Test")
                .email("test@mail.com")
                .login("Test")
                .birthday(LocalDate.of(2000, 12, 12))
                .build();

        assertThrows(NotFoundException.class, () -> inMemoryUserStorage.update(user));
    }

    @Test
    public void update_invalidLogin_throwConditionsNotMetException() {
        User user = User.builder()
                .name("Test")
                .email("test@mail.com")
                .login("Test")
                .birthday(LocalDate.of(2000, 12, 12))
                .build();
        inMemoryUserStorage.create(user);

        User userLoginWhitespace = User.builder()
                .id(1L)
                .login("Login with whitespace")
                .build();

        assertThrows(ConditionsNotMetException.class, () -> inMemoryUserStorage.update(userLoginWhitespace));
    }
}
