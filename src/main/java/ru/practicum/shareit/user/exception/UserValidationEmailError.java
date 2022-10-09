package ru.practicum.shareit.user.exception;

public class UserValidationEmailError extends RuntimeException {
    public UserValidationEmailError() {
        super("The user with your email already exists ");
    }
}
