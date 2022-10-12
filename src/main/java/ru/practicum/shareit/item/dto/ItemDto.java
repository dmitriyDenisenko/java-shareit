package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.*;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    private ItemRequest request;
    private BookingDtoItem nextBooking;
    private BookingDtoItem lastBooking;
    private List<CommentDto> comments;

}
