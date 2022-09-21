package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDto itemDto, int sharedUserId);

    ItemDto updateItem(ItemDto itemDto, int itemId, int sharedUserId);

    ItemDto getItemById(int id);

    List<ItemDto> getAllItemsForUser(int userId);

    List<ItemDto> searchItemByText(String text);
}
