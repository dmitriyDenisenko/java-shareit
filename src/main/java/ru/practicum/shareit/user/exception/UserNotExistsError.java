package ru.practicum.shareit.user.exception;

public class UserNotExistsError extends RuntimeException {
    public UserNotExistsError() {
        super("User not exists");
    }
}
