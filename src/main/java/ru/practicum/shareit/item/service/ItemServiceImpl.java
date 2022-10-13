package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.BookingForItem;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.CommentDtoMapper;
import ru.practicum.shareit.item.ItemDtoMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemsDifficileUsersException;
import ru.practicum.shareit.item.exception.UserIsNotOwnerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.exception.BadParametersException;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotExistsException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }


    @Transactional
    @Override
    public ItemDto saveItem(ItemDto itemDto, Long id) {
        Optional<User> user = userRepository.findById(Long.valueOf(id));
        if (user.isEmpty()) {
            throw new UserNotExistsException();
        }
        Item item = ItemDtoMapper.mapToItem(itemDto, user.get());
        if(itemDto.getRequestId() != null){
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(ItemRequestNotFoundException::new);
            item.setRequestId(itemRequest.getId());
        }
        item.setOwner(userRepository.findById(id).orElseThrow(UserNotExistsException::new).getId());
        return ItemDtoMapper.mapToItemDto(itemRepository.save(item));

    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long sharedUserId) {
        Optional<Item> itemOp = itemRepository.findById(itemId);
        if (itemOp.isEmpty()) {
            throw new ItemNotFoundException();
        }
        Item item = itemOp.get();
        if (!sharedUserId.equals(item.getOwner())) {
            throw new ItemsDifficileUsersException();
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemDtoMapper.mapToItemDto(itemRepository.save(item));
    }


    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        ItemDto itemDto = ItemDtoMapper.mapToItemDto(item);
        if (userId.equals(item.getOwner())) {
            setLastAndNextBookingDate(item, itemDto);
        }
        return setComments(itemDto);

    }

    @Override
    public List<ItemDto> getAllItemsForUser(Integer from, Integer size, Long userId) {
        if (from < 0) {
            throw new BadParametersException("Error! Your parameter from < 0");
        }
        User owner = userRepository.findById(userId).orElseThrow(UserNotExistsException::new);
        return itemRepository.findByOwner(owner.getId(),  PageRequest.of(from / size, size))
                .stream()
                .sorted(Comparator.comparing(Item::getId))
                .map(item -> setLastAndNextBookingDate(item, ItemDtoMapper.mapToItemDto(item)))
                .map(this::setComments)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemByText(Integer from, Integer size, String text) {
        if (from < 0) {
            throw new BadParametersException("Error! Parameter 'From' < 0 ");
        }
        if (text.equals("")) {
            return new ArrayList<>();
        }
        return itemRepository.search(text,  PageRequest.of(from / size, size))
                .stream()
                .map(ItemDtoMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto postComment(Long userId, Long itemId, CommentDto commentDto) {
        Comment comment = CommentDtoMapper.toComment(commentDto);
        if (comment.getText().isEmpty()) {
            throw new UserIsNotOwnerException("text is empty");
        }
        comment.setAuthor(userRepository.findById(userId)
                .orElseThrow(UserNotExistsException::new));
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        List<BookingForItem> bookings = bookingRepository.findAllByItem(item);
        BookingForItem booking = bookings.stream()
                .filter(bookingForItem -> Objects.equals(bookingForItem.getBooker().getId(), userId))
                .findAny()
                .orElseThrow(() -> new UserIsNotOwnerException("user not booking this item"));
        LocalDateTime time = booking.getEnd();
        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new UserIsNotOwnerException("user do not end booking;");
        }
        comment.setItem(item.getId());

        return CommentDtoMapper.toCommentDto(commentRepository.save(comment));
    }

    public ItemDto setLastAndNextBookingDate(Item item, ItemDto itemDto) {
        List<BookingForItem> bookings = bookingRepository.findAllByItem(item);
        BookingForItem last = bookings.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .max((booking, booking1) -> booking1.getStart().compareTo(booking.getStart()))
                .orElse(null);

        BookingForItem next = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min((booking, booking1) -> booking.getStart().compareTo(booking1.getStart()))
                .orElse(null);

        if (last != null) {
            itemDto.setLastBooking(BookingMapper.toBookingDtoItem(last));
        }
        if (next != null) {
            itemDto.setNextBooking(BookingMapper.toBookingDtoItem(next));
        }
        return itemDto;
    }

    public ItemDto setComments(ItemDto itemdto) {
        List<CommentDto> comments = findCommentsByItem(itemdto.getId())
                .stream()
                .map(CommentDtoMapper::toCommentDto)
                .collect(Collectors.toList());
        itemdto.setComments(comments);
        return itemdto;
    }

    public List<Comment> findCommentsByItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        return commentRepository.findCommentsByItem(item.getId());
    }
}
