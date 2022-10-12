package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoState;
import ru.practicum.shareit.booking.dto.BookingDtoUser;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingDtoUser create(long userId, long itemId, BookingDto bookingDto);

    BookingDtoUser approveStatus(long userId, long bookingId, boolean approved);

    BookingDtoUser getBookingById(long userId, long bookingId);

    List<BookingDtoState> getBookingCurrentUser(long userId, State stateEnum);

    List<BookingDtoState> getBookingCurrentOwner(long userId, State stateEnum);
}