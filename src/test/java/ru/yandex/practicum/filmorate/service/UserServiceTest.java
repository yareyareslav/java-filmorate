package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserService userService;

    @BeforeEach
    public void init() {
        userService = new UserService();
    }

    @Test
    public void findAll_returnUserArray() {
        User user = User.builder()
                .name("Test")
                .email("test@mail.com")
                .login("Test")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        assertEquals(0, userService.findAll().size());

        userService.create(user);
        userService.create(user);
        userService.create(user);

        assertEquals(3, userService.findAll().size());
    }

    @Test
    public void create_validData_returnUserWithId() {
        User user = User.builder()
                .name("Test")
                .email("test@mail.com")
                .login("Test")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        User createdUser = userService.create(user);

        assertTrue(userService.findAll().contains(createdUser));
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

        User createdUser = userService.create(user);

        assertTrue(userService.findAll().contains(createdUser));
        assertEquals(1, createdUser.getId());
        assertEquals(user.getLogin(), createdUser.getName(), "Name must be equal to login");
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getLogin(), createdUser.getLogin());
        assertEquals(user.getBirthday(), createdUser.getBirthday());
    }

    @Test
    public void create_invalidEmail_throwConditionsNotMetException() {
        User userMissingA = User.builder()
                .name("Test")
                .email("invalid-email.com")
                .login("Test")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();
        User userEmailNull = User.builder()
                .name("Test")
                .login("Test")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> userService.create(userMissingA));
        assertThrows(ConditionsNotMetException.class, () -> userService.create(userEmailNull));
    }

    @Test
    public void create_invalidLogin_throwConditionsNotMetException() {
        User userLoginNull = User.builder()
                .name("Test")
                .email("test@mail.com")
                .login("")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();
        User userLoginWhitespace = User.builder()
                .name("Test")
                .email("test@mail.com")
                .login("Login with whitespace")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> userService.create(userLoginNull));
        assertThrows(ConditionsNotMetException.class, () -> userService.create(userLoginWhitespace));
    }

    @Test
    public void create_invalidBirthday_throwConditionsNotMetException() {
        User userBirthdayInFuture = User.builder()
                .name("Test")
                .email("test@mail.com")
                .login("Test")
                .birthday(LocalDate.of(10000, 12, 12))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> userService.create(userBirthdayInFuture));
    }

    @Test
    public void update_validData_returnUpdatedUser() {
        User user = User.builder()
                .name("Test")
                .email("test@mail.com")
                .login("Test")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        User createdUser = userService.create(user);

        User userToUpdate = User.builder()
                .id(1L)
                .name("Test Update")
                .email("update-test@mail.com")
                .login("Test-Update-Login")
                .birthday(LocalDate.of(2000, 12, 12))
                .build();

        User updatedUser = userService.update(userToUpdate);

        assertSame(createdUser, updatedUser, "Users have the same link");
        assertTrue(userService.findAll().contains(updatedUser));
        assertEquals(1, userService.findAll().size(), "User list must contain only one object");
        assertEquals(createdUser.getId(), updatedUser.getId());
        assertEquals(userToUpdate.getId(), updatedUser.getId());
        assertEquals(userToUpdate.getName(), updatedUser.getName());
        assertEquals(userToUpdate.getLogin(), updatedUser.getLogin());
        assertEquals(userToUpdate.getEmail(), updatedUser.getEmail());
        assertEquals(userToUpdate.getBirthday(), updatedUser.getBirthday());
    }

    @Test
    public void update_idNull_throwConditionsNotMetException() {
        User user = User.builder()
                .name("Test")
                .email("test@mail.com")
                .login("Test")
                .birthday(LocalDate.of(2000, 12, 12))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> userService.update(user));
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

        assertThrows(NotFoundException.class, () -> userService.update(user));
    }

    @Test
    public void update_invalidLogin_throwConditionsNotMetException() {
        User user = User.builder()
                .name("Test")
                .email("test@mail.com")
                .login("Test")
                .birthday(LocalDate.of(2000, 12, 12))
                .build();
        userService.create(user);

        User userLoginBlank = User.builder()
                .id(1L)
                .login("")
                .build();
        User userLoginWhitespace = User.builder()
                .id(1L)
                .login("Login with whitespace")
                .build();

        assertThrows(ConditionsNotMetException.class, () -> userService.update(userLoginBlank));
        assertThrows(ConditionsNotMetException.class, () -> userService.update(userLoginWhitespace));
    }

    @Test
    public void update_invalidEmail_throwConditionsNotMetException() {
        User user = User.builder()
                .name("Test")
                .email("test@mail.com")
                .login("Test")
                .birthday(LocalDate.of(2000, 12, 12))
                .build();
        userService.create(user);

        User userEmailMissingA = User.builder()
                .id(1L)
                .email("invalid-mail.ru")
                .build();

        assertThrows(ConditionsNotMetException.class, () -> userService.update(userEmailMissingA));
    }

    @Test
    public void update_invalidBirthday_throwConditionsNotMetException() {
        User user = User.builder()
                .name("Test")
                .email("test@mail.com")
                .login("Test")
                .birthday(LocalDate.of(2000, 12, 12))
                .build();
        userService.create(user);

        User userBirthdayInFuture = User.builder()
                .id(1L)
                .birthday(LocalDate.of(10000, 12, 12))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> userService.update(userBirthdayInFuture));
    }
}
