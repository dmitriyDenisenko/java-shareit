package ru.practicum.shareit.booking.service;

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
        if (!(item.getAvailable())) {
            throw new ItemNotAvailableException("item not availible");
        }
        if (Objects.equals(item.getOwner(), userId)) {
            throw new UserIsNotOwnerException("you can not booking this item");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new TimeStartAndEndException();
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(userRepository.findById(userId)
                .orElseThrow(UserNotExistsException::new));
        booking.setStatus(Status.WAITING);
        return BookingMapper.toBookingDtoToUser(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoUser approveStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("booking not found"));
        if (!(booking.getStatus().equals(Status.WAITING))) {
            throw new BookingNotChangeStatusException("status can not be change");
        }
        if (!Objects.equals(userId, booking.getItem().getOwner())) {
            throw new UserIsNotOwnerException("user not owner this item and can not approve status");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDtoToUser(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoUser getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("booking not found"));
        if (!Objects.equals(userId, booking.getItem().getOwner()) && !Objects.equals(userId, booking.getBooker().getId())) {
            throw new UserIsNotOwnerException("user not owner this item and can not get this booking");
        }
        return BookingMapper.toBookingDtoToUser(booking);
    }

    @Override
    public List<BookingDtoState> getBookingCurrentUser(Long userId, State stateEnum, Integer from, Integer size) {
        if (from < 0) {
            throw new BadParametersException("Error. From < 0");
        }
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
        if (from < 0) {
            throw new BadParametersException("Error. From < 0");
        }
        User owner = userRepository.findById(userId).orElseThrow(UserNotExistsException::new);
        return bookingRepository.findAllByItemOwner(owner.getId(), PageRequest.of(from / size, size,
                        Sort.by(Sort.Direction.DESC, "start")))
                .stream()
                .map(BookingMapper::toBookingDtoState)
                .filter(bookingDtoState -> bookingDtoState.getStates().contains(stateEnum))
                .sorted(Comparator.comparing(BookingDtoState::getStart).reversed())
                .collect(Collectors.toList());
    }
}