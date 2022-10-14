package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoState;
import ru.practicum.shareit.booking.dto.BookingDtoUser;
import ru.practicum.shareit.booking.exception.BookingNotChangeStatusException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.TimeStartAndEndException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UserIsNotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.exception.BadParametersException;
import ru.practicum.shareit.user.exception.UserNotExistsException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public BookingDtoUser create(Long userId, Long itemId, BookingDto bookingDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        validateCreatingBooking(item, userId, bookingDto);
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(userRepository.findById(userId)
                .orElseThrow(UserNotExistsException::new));
        booking.setStatus(Status.WAITING);
        log.info("Booking {} has been successfully prepared for saving to the database", booking.getId());
        return BookingMapper.toBookingDtoToUser(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoUser approveStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("booking not found"));
        validateApproveStatus(booking, userId);
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        log.info("Booking {} has been successfully approve status", bookingId);
        return BookingMapper.toBookingDtoToUser(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoUser getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("booking not found"));
        validateGettingBookById(booking, userId);
        return BookingMapper.toBookingDtoToUser(booking);
    }

    @Override
    public List<BookingDtoState> getBookingCurrentUser(Long userId, State stateEnum, Integer from, Integer size) {
        validatePageParameters(from, size);
        User booker = userRepository.findById(userId).orElseThrow(UserNotExistsException::new);
        return bookingRepository.findAllByBooker(booker, PageRequest.of(from / size, size,
                        Sort.by(Sort.Direction.DESC, "start")))
                .stream()
                .map(BookingMapper::toBookingDtoState)
                .filter(bookingDtoState -> bookingDtoState.getStates().contains(stateEnum))
                .sorted(Comparator.comparing(BookingDtoState::getStart).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoState> getBookingCurrentOwner(Long userId, State stateEnum, Integer from, Integer size) {
        validatePageParameters(from, size);
        User owner = userRepository.findById(userId).orElseThrow(UserNotExistsException::new);
        return bookingRepository.findAllByItemOwner(owner.getId(), PageRequest.of(from / size, size,
                        Sort.by(Sort.Direction.DESC, "start")))
                .stream()
                .map(BookingMapper::toBookingDtoState)
                .filter(bookingDtoState -> bookingDtoState.getStates().contains(stateEnum))
                .sorted(Comparator.comparing(BookingDtoState::getStart).reversed())
                .collect(Collectors.toList());
    }

    private void validateCreatingBooking(Item item, Long userId, BookingDto bookingDto) {
        if (!(item.getAvailable())) {
            log.warn("Item (id: {}) have available false", item.getId());
            throw new ItemNotAvailableException("item not available");
        }
        if (Objects.equals(item.getOwner(), userId)) {
            log.warn("Item (id: {}) have owner:{} , but user {} trying to create a booking",
                    item.getId(), item.getOwner(), userId);
            throw new UserIsNotOwnerException("you can not booking this item");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            log.warn("Booking start: {}, after booking end: {}",
                    bookingDto.getStart(), bookingDto.getEnd());
            throw new TimeStartAndEndException();
        }
    }

    private void validateApproveStatus(Booking booking, Long userId) {
        if (!(booking.getStatus().equals(Status.WAITING))) {
            log.warn("Status for booking {} can`t be change", booking.getId());
            throw new BookingNotChangeStatusException("status can not be change");
        }
        if (!Objects.equals(userId, booking.getItem().getOwner())) {
            log.warn("User {} not owner {} of item {}", userId,
                    booking.getItem().getOwner(), booking.getItem().getId());
            throw new UserIsNotOwnerException("user not owner this item and can not approve status");
        }
    }

    private void validateGettingBookById(Booking booking, Long userId) {
        if (!Objects.equals(userId, booking.getItem().getOwner()) && !Objects.equals(userId,
                booking.getBooker().getId())) {
            log.warn("User {} not owner {} booking {}. Can`t get this book",
                    userId, booking.getBooker(), booking.getId());
            throw new UserIsNotOwnerException("user not owner this item and can not get this booking");
        }
    }

    private void validatePageParameters(Integer from, Integer size) {
        if (from < 0 || size == 0) {
            log.warn("From = {}; Size = {}", from, size);
            throw new BadParametersException("Error! You giving bad Parameters");
        }
    }
}