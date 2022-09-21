package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Data
public class ItemRequest {
    private int id;
    private String description;
    private User requester;
    private LocalDate created;
}
