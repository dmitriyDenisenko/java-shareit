package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoState;
import ru.practicum.shareit.booking.dto.BookingDtoUser;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingDtoUser create(Long userId, Long itemId, BookingDto bookingDto);

    BookingDtoUser approveStatus(Long userId, Long bookingId, Boolean approved);

    BookingDtoUser getBookingById(Long userId, Long bookingId);

    List<BookingDtoState> getBookingCurrentUser(Long userId, State stateEnum, Integer from, Integer size);

    List<BookingDtoState> getBookingCurrentOwner(Long userId, State stateEnum, Integer from, Integer size);
}