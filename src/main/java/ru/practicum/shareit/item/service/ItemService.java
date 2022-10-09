package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDto itemDto, Long sharedUserId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long sharedUserId);

    ItemDto getItemById(Long id);

    List<ItemDto> getAllItemsForUser(Long userId);

    List<ItemDto> searchItemByText(String text);
}
