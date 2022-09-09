package ru.practicum.shareit.user;

public class UserValidationEmailError extends RuntimeException {
    public UserValidationEmailError() {
        super("The user with your email already exists ");
    }
}
