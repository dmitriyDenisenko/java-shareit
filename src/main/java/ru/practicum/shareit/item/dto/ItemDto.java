package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class ItemDto {
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    @NotNull
    private String description;
    @NotNull
    private Boolean available;

    public ItemDto(int id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
