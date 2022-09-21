package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemRepository {
    ItemDto saveItem(ItemDto itemDto, int user);

    ItemDto updateItem(ItemDto itemDto, int itemId, int sharedUserId);

    ItemDto getItemDtoById(int id);

    List<ItemDto> getAllItemsForUser(int userId);

    List<ItemDto> searchItemByText(String text);
}
