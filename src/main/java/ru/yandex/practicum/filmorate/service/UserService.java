package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
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
        User user = userStorage.findOne(id);
        if (user == null) {
            throw new NotFoundException("User not found. User id: " + id);
        }

        User friend = userStorage.findOne(friendId);
        if (friend == null) {
            throw new NotFoundException("Friend not found. Friend id: " + friendId);
        }

        log.info("Add friend ended. User id: {}, Friend id: {}", id, friendId);
        return user.getFriendsIds().add(friendId)
                && friend.getFriendsIds().add(id);
    }

    public boolean removeFriend(Long id, Long friendId) {
        log.info("Remove friend initiated. User id: {}, Friend id: {}", id, friendId);
        User user = userStorage.findOne(id);
        if (user == null) {
            throw new NotFoundException("User not found. User id: " + id);
        }

        User friend = userStorage.findOne(friendId);
        if (friend == null) {
            throw new NotFoundException("Friend not found. Friend id: " + friendId);
        }

        log.info("Remove friend ended. User id: {}, Friend id: {}", id, friendId);
        return user.getFriendsIds().remove(friendId)
                && friend.getFriendsIds().remove(id);
    }

    public Collection<User> getFriends(Long id) {
        log.info("Get friends initiated. User id: {}", id);
        User user = userStorage.findOne(id);
        if (user == null) {
            throw new NotFoundException("User not found. User id: " + id);
        }

        log.info("Get friends ended. User id: {}", id);
        return user.getFriendsIds()
                .stream()
                .map(userStorage::findOne)
                .toList();
    }

    public Collection<User> getCommonFriends(Long id, Long friendId) {
        log.info("Get common friend initiated. User id: {}, Friend id: {}", id, friendId);
        User user = userStorage.findOne(id);
        if (user == null) {
            throw new NotFoundException("User not found. User id: " + id);
        }

        User friend = userStorage.findOne(friendId);
        if (friend == null) {
            throw new NotFoundException("Friend not found. Friend id: " + friendId);
        }

        Set<Long> commonFriendsIds = new HashSet<>(user.getFriendsIds());
        commonFriendsIds.retainAll(friend.getFriendsIds());

        log.info("Get common friend ended. User id: {}, Friend id: {}", id, friendId);
        return commonFriendsIds
                .stream()
                .map(userStorage::findOne)
                .collect(Collectors.toSet());
    }

}
