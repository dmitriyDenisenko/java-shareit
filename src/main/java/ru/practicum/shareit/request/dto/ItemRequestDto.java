package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    private int id;
    private String description;
    private User requester;
    private LocalDate created;

    public ItemRequestDto(int id, String description, User requester, LocalDate created) {
        this.id = id;
        this.description = description;
        this.requester = requester;
        this.created = created;
    }
}
