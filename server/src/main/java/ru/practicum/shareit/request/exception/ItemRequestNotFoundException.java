package ru.practicum.shareit.request.exception;

public class ItemRequestNotFoundException extends RuntimeException {
    public ItemRequestNotFoundException() {
        super("ItemRequest not found!");
    }
}
