package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {
    public interface UpdateUserInfo {}

    public interface CreateUserInfo {}

    @NotNull(groups = UpdateUserInfo.class)
    private Long id;

    @Email(groups = { CreateUserInfo.class, UpdateUserInfo.class })
    @NotNull(groups = CreateUserInfo.class)
    private String email;

    @NotBlank(groups = CreateUserInfo.class)
    private String login;

    private String name;

    @NotNull(groups = CreateUserInfo.class)
    @Past(groups = { CreateUserInfo.class, UpdateUserInfo.class })
    private LocalDate birthday;
}
