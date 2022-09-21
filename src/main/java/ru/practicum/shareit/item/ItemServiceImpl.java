package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepositoryImpl itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemDto saveItem(ItemDto itemDto, int id) {
        return itemRepository.saveItem(itemDto, id);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int itemId, int sharedUserId) {
        return itemRepository.updateItem(itemDto, itemId, sharedUserId);
    }

    @Override
    public ItemDto getItemById(int id) {
        return itemRepository.getItemById(id);
    }

    @Override
    public List<ItemDto> getAllItemsForUser(int userId) {
        return itemRepository.getAllItemsForUser(userId);
    }

    @Override
    public List<ItemDto> searchItemByText(String text) {
        return itemRepository.searchItemByText(text);
    }
}
