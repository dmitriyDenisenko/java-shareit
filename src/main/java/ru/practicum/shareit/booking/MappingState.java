package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.exception.BookingDtoBadStateException;
import ru.practicum.shareit.booking.model.State;

@UtilityClass
public class MappingState {
    public static State mapStatus(String text) {
        State stateEnum;
        try {
            stateEnum = State.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BookingDtoBadStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return stateEnum;
    }
}