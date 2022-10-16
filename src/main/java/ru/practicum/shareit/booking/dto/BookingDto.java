package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;

    @Future
    private LocalDateTime start;

    @Future
    private LocalDateTime end;

    private Long itemId;

    private Long booker;

    private Status status;
}