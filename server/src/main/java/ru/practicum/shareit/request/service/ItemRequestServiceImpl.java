package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.exception.BadParametersException;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotExistsException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    @Override
    public RequestDto createRequest(Long sharerUserId, RequestDto requestDto) {
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(requestDto);
        itemRequest.setRequester(checkAndGenerateUser(sharerUserId).getId());
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.mapToRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAll(Long sharerUserId, Integer from, Integer size) {
        User user = checkAndGenerateUser(sharerUserId);
        log.info("All successful for getAll");
        return itemRequestRepository.findAllByRequesterIsNot(user.getId(), PageRequest
                        .of(from / size, size, Sort.by(Sort.Direction.DESC, "created")))
                .stream()
                .map(this::createItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getByRequester(Long sharerUserId) {
        User user = checkAndGenerateUser(sharerUserId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterOrderByCreatedDesc(user.getId());
        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        for (ItemRequest itemRequest : requests) {
            itemRequestsDto.add(createItemRequestDto(itemRequest));
        }
        return itemRequestsDto;
    }

    @Override
    public ItemRequestDto getById(Long sharerUserId, Long requestId) {
        checkAndGenerateUser(sharerUserId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(ItemRequestNotFoundException::new);
        log.info("All successful for get by id {}", requestId);
        return createItemRequestDto(itemRequest);
    }

    public User checkAndGenerateUser(long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotExistsException::new);
    }

    private ItemRequestDto createItemRequestDto(ItemRequest itemRequest) {
        List<ItemDto> items = itemRepository
                .findByRequestId(itemRequest.getId())
                .stream()
                .map(ItemDtoMapper::mapToItemDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.mapToItemRequestDto(itemRequest, items);
    }

}
