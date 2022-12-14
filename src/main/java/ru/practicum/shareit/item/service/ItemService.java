package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDto itemDto, Long sharedUserId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long sharedUserId);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getAllItemsForUser(Integer from, Integer size, Long userId);

    List<ItemDto> searchItemByText(Integer from, Integer size, String text);

    CommentDto postComment(Long userId, Long itemId, CommentDto commentDto);
}
