package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.exception.UserNotExistsError;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;


    @Override
    public ItemDto saveItem(ItemDto itemDto, int id) {
        Optional<User> user = userRepository.findById(Long.valueOf(id));
        if(user.isPresent()){
            Item item = ItemDtoMapper.mapToItem(itemDto,user.get());
            itemRepository.save(item);
        }
        throw new UserNotExistsError();
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int itemId, int sharedUserId) {
        return null;
    }

    @Override
    public ItemDto getItemById(int id) {
        Optional<Item> item = itemRepository.findById(Long.valueOf(id));
        if(item.isPresent()){
            return ItemDtoMapper.mapToItemDto(item.get());
        }
        throw new ItemNotFoundError();

    }

    @Override
    public List<ItemDto> getAllItemsForUser(int userId) {
        return null;
    }

    @Override
    public List<ItemDto> searchItemByText(String text) {
        return null;
    }
}
