package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public boolean addFriend(Long id, Long friendId) {
        User user = userStorage.findOne(id);
        if (user == null) {
            throw new NotFoundException("User not found. User id: " + id);
        }

        User friend = userStorage.findOne(friendId);
        if (friend == null) {
            throw new NotFoundException("Friend not found. Friend id: " + friendId);
        }

        return user.getFriendsIds().add(friendId)
                && friend.getFriendsIds().add(id);
    }

    public boolean removeFriend(Long id, Long friendId) {
        User user = userStorage.findOne(id);
        if (user == null) {
            throw new NotFoundException("User not found. User id: " + id);
        }

        User friend = userStorage.findOne(friendId);
        if (friend == null) {
            throw new NotFoundException("Friend not found. Friend id: " + friendId);
        }

        return user.getFriendsIds().remove(friendId)
                && friend.getFriendsIds().remove(id);
    }

    public Collection<User> getCommonFriends(Long id, Long friendId) {
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

        return commonFriendsIds
                .stream()
                .map(userStorage::findOne)
                .collect(Collectors.toSet());
    }

}
