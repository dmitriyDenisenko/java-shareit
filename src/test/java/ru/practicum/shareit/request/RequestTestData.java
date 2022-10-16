package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RequestTestData {
    public static final RequestDto requestDto = RequestDto.builder().id(2L)
            .description("itemRequestCreated").requesterId(3L)
            .created(LocalDateTime.now())
            .build();

    public static final ItemRequestDto itemRequestDto = ItemRequestDto.builder().id(1L)
            .description("itemRequest1")
            .created(LocalDateTime.of(2022, 10, 14, 12, 15)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
            .items(new ArrayList<>())
            .build();

    public static final ItemRequestDto itemRequestDtoCreated = ItemRequestDto.builder().id(2L)
            .description("itemRequestCreated")
            .created(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
            .items(new ArrayList<>())
            .build();
}