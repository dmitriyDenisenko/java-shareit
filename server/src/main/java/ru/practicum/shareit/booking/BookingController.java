package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoState;
import ru.practicum.shareit.booking.dto.BookingDtoUser;
import ru.practicum.shareit.booking.exception.BookingDtoBadStateException;
import ru.practicum.shareit.booking.exception.ErrorResponse;
import ru.practicum.shareit.booking.exception.TimeStartAndEndException;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.exception.ItemNotFoundException;


import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping()
    BookingDtoUser create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody BookingDto bookingDto) {
        log.info("create booking. User_id: {}; Booking_id: {}", userId, bookingDto.getId());
        return bookingService.create(userId, bookingDto.getItemId(), bookingDto);
    }

    @PatchMapping("/{bookingId}")
    BookingDtoUser approveStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId,
                                 @RequestParam Boolean approved) {
        log.info("Approve status for booking {}", bookingId);
        return bookingService.approveStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    BookingDtoUser getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long bookingId) {
        log.info("get booking id={}", bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping()
    List<BookingDtoState> getBookingCurrentUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @RequestParam(defaultValue = "1") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("get booking current user id ={}", userId);
        State stateEnum = MappingState.mapStatus(state);
        return bookingService.getBookingCurrentUser(userId, stateEnum, from, size);
    }

    @GetMapping("/owner")
    List<BookingDtoState> getBookingCurrentOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(defaultValue = "1") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        log.info("get booking current owner id ={}", userId);
        State stateEnum = MappingState.mapStatus(state);
        return bookingService.getBookingCurrentOwner(userId, stateEnum, from, size);
    }

    @ExceptionHandler(value
            = {BookingDtoBadStateException.class, TimeStartAndEndException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameterException(RuntimeException e) {
        log.warn("The action was not completed successfully");
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFindItemOfBooking(ItemNotFoundException e) {
        log.warn("The action was not completed successfully");
        return new ErrorResponse(e.getMessage());
    }
}