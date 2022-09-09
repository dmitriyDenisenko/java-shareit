package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    private String name;
    private String description;
    private boolean available;
    private int request;

    public ItemDto(String name, String description, boolean available, int request){
        this.name = name;
        this.available = available;
        this.request = request;
    }
}
