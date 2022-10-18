package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.dto.BookingDtoState;
import ru.practicum.shareit.booking.dto.BookingDtoUser;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingForItem;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemDtoMapper;
import ru.practicum.shareit.user.UserDtoMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;

@UtilityClass
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .booker(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(null)
                .booker(null)
                .status(bookingDto.getStatus())
                .build();
    }

    public static BookingDtoState toBookingDtoState(Booking booking) {
        ArrayList<State> states = new ArrayList<>();
        states.add(State.ALL);
        if (booking.getStatus().equals(Status.WAITING)) {
            states.add(State.WAITING);
        }
        if (booking.getStatus().equals(Status.REJECTED)) {
            states.add(State.REJECTED);
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            states.add(State.PAST);
        }
        if (booking.getStart().isAfter(LocalDateTime.now())) {
            states.add(State.FUTURE);
        }
        if (booking.getStart().isBefore(LocalDateTime.now()) && booking.getEnd().isAfter(LocalDateTime.now())) {
            states.add(State.CURRENT);
        }

        return BookingDtoState.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemDtoMapper.mapToItemDto(booking.getItem()))
                .booker(UserDtoMapper.mapToUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .states(states)
                .build();
    }

    public static BookingDtoUser toBookingDtoToUser(Booking booking) {
        return BookingDtoUser.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemDtoMapper.mapToItemDto(booking.getItem()))
                .booker(UserDtoMapper.mapToUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static BookingDtoItem toBookingDtoItem(BookingForItem bookingForItem) {
        return BookingDtoItem.builder()
                .id(bookingForItem.getId())
                .start(bookingForItem.getStart())
                .end(bookingForItem.getEnd())
                .itemId(bookingForItem.getId())
                .bookerId(bookingForItem.getBooker().getId())
                .build();
    }
}