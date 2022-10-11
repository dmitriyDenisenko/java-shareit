package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
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

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping()
    BookingDtoUser create(@NotBlank @RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody @Valid BookingDto bookingDto) {
        log.info("create booking. UserId: {}; Booking id: {}",userId,bookingDto.getId());
        return bookingService.create(userId, bookingDto.getItemId(), bookingDto);
    }

    @PatchMapping("/{bookingId}")
    BookingDtoUser approveStatus(@NotBlank @RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable Long bookingId,
                                 @RequestParam boolean approved) {
        return bookingService.approveStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    BookingDtoUser getBookingById(@NotBlank @RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable long bookingId) {
        log.info("get booking id={}", bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping()
    List<BookingDtoState> getBookingCurrentUser(@NotBlank @RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        log.info("get booking current user id ={}", userId);
        State stateEnum = MappingState.mapStatus(state);
        return bookingService.getBookingCurrentUser(userId, stateEnum);
    }

    @GetMapping("/owner")
    List<BookingDtoState> getBookingCurrentOwner(@NotBlank @RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        log.info("get booking current owner id ={}", userId);
        State stateEnum = MappingState.mapStatus(state);
        return bookingService.getBookingCurrentOwner(userId, stateEnum);
    }

    @ExceptionHandler(value
            = {BookingDtoBadStateException.class, TimeStartAndEndException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameterException(RuntimeException e) {
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFindItemOfBooking(ItemNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}