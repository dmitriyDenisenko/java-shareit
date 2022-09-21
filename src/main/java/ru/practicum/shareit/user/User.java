package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class User {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}
