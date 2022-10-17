package ru.practicum.shareit.booking;

public class TimeStartAndEndException extends RuntimeException {
    public TimeStartAndEndException() {
        super("Start time and end time are not correct");
    }
}
