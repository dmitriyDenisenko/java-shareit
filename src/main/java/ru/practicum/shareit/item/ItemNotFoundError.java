package ru.practicum.shareit.item;

public class ItemNotFoundError extends RuntimeException{
    public ItemNotFoundError(){
        super("Item not found");
    }
}
