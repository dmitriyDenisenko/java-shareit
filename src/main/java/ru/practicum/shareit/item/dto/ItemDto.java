package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.*;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    @NotNull
    private String description;
    @NotNull
    @Pattern(regexp = "^true$|^false$", message = "allowed input: true or false")
    private String available;

    public ItemDto(int id, String name, String description, String available){
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
