package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface ItemRequestService {
    RequestDto createRequest(Long sharerUserId, RequestDto requestDto);

    List<ItemRequestDto> getAll(Long sharerUserId, Integer from, Integer size);

    List<ItemRequestDto> getByRequester(Long sharerUserId);

    ItemRequestDto getById(Long sharerUserId, Long requestId);
}
