package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserStorage userStorage;

    private UserService userService;

    @BeforeEach
    public void init() {
        userService = new UserService(userStorage);
    }

    private User validUser() {
        return User.builder()
                .name("Test")
                .email("test@mail.com")
                .login("Test")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();
    }

    @Test
    public void create_nameIsNull_returnUserWithIdAndName() {
        User user = User.builder()
                .email("test@mail.com")
                .login("Test")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        when(userStorage.create(any(User.class))).thenAnswer(invocation -> {
            User created = invocation.getArgument(0);
            created.setId(1L);
            return created;
        });

        User createdUser = userService.create(user);

        assertEquals(1L, createdUser.getId());
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

        assertThrows(ConditionsNotMetException.class, () -> userService.create(userLoginWhitespace));
        verifyNoInteractions(userStorage);
    }

    @Test
    public void update_validData_returnUpdatedUser() {
        User existingUser = validUser();
        existingUser.setId(1L);

        User userToUpdate = User.builder()
                .id(1L)
                .name("Test Update")
                .email("update-test@mail.com")
                .login("Test-Update-Login")
                .birthday(LocalDate.of(2000, 12, 12))
                .build();

        when(userStorage.findOne(1L)).thenReturn(Optional.of(existingUser));
        when(userStorage.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.update(userToUpdate);

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

        when(userStorage.findOne(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(user));
    }

    @Test
    public void update_invalidLogin_throwConditionsNotMetException() {
        User existingUser = validUser();
        existingUser.setId(1L);

        when(userStorage.findOne(1L)).thenReturn(Optional.of(existingUser));

        User userLoginWhitespace = User.builder()
                .id(1L)
                .login("Login with whitespace")
                .build();

        assertThrows(ConditionsNotMetException.class, () -> userService.update(userLoginWhitespace));
        verify(userStorage, never()).update(any());
    }

    @Test
    public void addFriend_noSuchFriend_returnTrue() {
        when(userStorage.findOne(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(userStorage.findOne(2L)).thenReturn(Optional.of(User.builder().id(2L).build()));
        when(userStorage.addFriend(1L, 2L)).thenReturn(true);

        assertTrue(userService.addFriend(1L, 2L));
        verify(userStorage).addFriend(1L, 2L);
    }

    @Test
    public void addFriend_hasSuchFriend_returnFalse() {
        when(userStorage.findOne(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(userStorage.findOne(2L)).thenReturn(Optional.of(User.builder().id(2L).build()));
        when(userStorage.addFriend(1L, 2L)).thenReturn(false);

        assertFalse(userService.addFriend(1L, 2L));
    }

    @Test
    public void addFriend_sameIds_throwConditionsNotMetException() {
        assertThrows(ConditionsNotMetException.class, () -> userService.addFriend(1L, 1L));
        verifyNoInteractions(userStorage);
    }

    @Test
    public void addFriend_invalidId_throwsNotFoundException() {
        when(userStorage.findOne(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(userStorage.findOne(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.addFriend(1L, 999L));
        assertThrows(NotFoundException.class, () -> userService.addFriend(999L, 1L));
    }

    @Test
    public void removeFriend_hasSuchFriend_returnTrue() {
        when(userStorage.findOne(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(userStorage.findOne(2L)).thenReturn(Optional.of(User.builder().id(2L).build()));
        when(userStorage.deleteFriend(1L, 2L)).thenReturn(true);

        assertTrue(userService.removeFriend(1L, 2L));
    }

    @Test
    public void removeFriend_noSuchFriend_returnFalse() {
        when(userStorage.findOne(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(userStorage.findOne(2L)).thenReturn(Optional.of(User.builder().id(2L).build()));
        when(userStorage.deleteFriend(1L, 2L)).thenReturn(false);

        assertFalse(userService.removeFriend(1L, 2L));
    }

    @Test
    public void removeFriend_invalidId_throwsNotFoundException() {
        when(userStorage.findOne(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.removeFriend(999L, 1L));
    }

    @Test
    public void getFriends_returnCollectionOfFriends() {
        User user = User.builder().id(1L).build();
        User friend1 = User.builder().id(2L).name("Friend 1").build();
        User friend2 = User.builder().id(3L).name("Friend 2").build();

        when(userStorage.findOne(1L)).thenReturn(Optional.of(user));
        when(userStorage.findFriends(1L)).thenReturn(List.of(friend1, friend2));

        Collection<User> friends = userService.getFriends(1L);

        assertEquals(2, friends.size());
        assertTrue(friends.contains(friend1));
        assertTrue(friends.contains(friend2));
    }

    @Test
    public void getFriends_invalidId_throwsNotFoundException() {
        when(userStorage.findOne(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getFriends(999L));
    }

    @Test
    public void getCommonFriends_returnCollectionOfCommonFriends() {
        User friend1 = User.builder().id(3L).name("Friend 1").build();

        when(userStorage.findOne(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(userStorage.findOne(2L)).thenReturn(Optional.of(User.builder().id(2L).build()));
        when(userStorage.findCommonFriends(1L, 2L)).thenReturn(List.of(friend1));

        Collection<User> commonFriends = userService.getCommonFriends(1L, 2L);

        assertEquals(1, commonFriends.size());
        assertTrue(commonFriends.contains(friend1));
    }

    @Test
    public void getCommonFriends_invalidId_throwsNotFoundException() {
        when(userStorage.findOne(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(userStorage.findOne(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getCommonFriends(1L, 999L));
        assertThrows(NotFoundException.class, () -> userService.getCommonFriends(999L, 1L));
    }
}
