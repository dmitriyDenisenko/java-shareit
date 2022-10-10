package ru.practicum.shareit.user.exception;

public class UserNotExistsException extends RuntimeException {
    public UserNotExistsException() {
        super("User not exists");
    }
}
