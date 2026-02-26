package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private User checkUserExists(final long id) {
        return userStorage
                .findOne(id)
                .orElseThrow(() -> new NotFoundException("User not found. User id: " + id));
    }

    private void checkUsersAreDifferent(final long user1Id, final long user2Id) {
        if (user1Id == user2Id) {
            throw new ConditionsNotMetException("Ids of users must be different");
        }
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(final User user) {
        return userStorage.create(user);
    }

    public User update(final User user) {
        return userStorage.update(user);
    }

    public boolean addFriend(Long id, Long friendId) {
        log.info("Add friend initiated. User id: {}, Friend id: {}", id, friendId);

        checkUsersAreDifferent(id, friendId);

        User user = checkUserExists(id);
        User friend = checkUserExists(friendId);

        log.info("Add friend ended. User id: {}, Friend id: {}", id, friendId);
        return user.getFriendsIds().add(friendId)
                && friend.getFriendsIds().add(id);
    }

    public boolean removeFriend(Long id, Long friendId) {
        log.info("Remove friend initiated. User id: {}, Friend id: {}", id, friendId);

        checkUsersAreDifferent(id, friendId);

        User user = checkUserExists(id);
        User friend = checkUserExists(friendId);

        log.info("Remove friend ended. User id: {}, Friend id: {}", id, friendId);
        return user.getFriendsIds().remove(friendId)
                && friend.getFriendsIds().remove(id);
    }

    public Collection<User> getFriends(Long id) {
        log.info("Get friends initiated. User id: {}", id);

        User user = checkUserExists(id);

        log.info("Get friends ended. User id: {}", id);
        return user.getFriendsIds()
                .stream()
                .map(userStorage::findOne)
                .flatMap(Optional::stream)
                .toList();
    }

    public Collection<User> getCommonFriends(Long id, Long friendId) {
        log.info("Get common friend initiated. User id: {}, Friend id: {}", id, friendId);

        checkUsersAreDifferent(id, friendId);

        User user = checkUserExists(id);
        User friend = checkUserExists(friendId);

        Set<Long> commonFriendsIds = new HashSet<>(user.getFriendsIds());
        commonFriendsIds.retainAll(friend.getFriendsIds());

        log.info("Get common friend ended. User id: {}, Friend id: {}", id, friendId);
        return commonFriendsIds
                .stream()
                .map(userStorage::findOne)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

}
