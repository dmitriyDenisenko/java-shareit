package ru.practicum.shareit.user;

public class UserNotExistsError extends RuntimeException {
    public UserNotExistsError() {
        super("User not exists");
    }
}
