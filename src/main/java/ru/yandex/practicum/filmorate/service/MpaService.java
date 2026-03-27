package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public MpaService(@Qualifier("mpaDbStorage") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Collection<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    public Mpa findOne(Long id) {
        return mpaStorage
                .findOne(id)
                .orElseThrow(() -> new NotFoundException("Mpa was not found"));
    }
}
