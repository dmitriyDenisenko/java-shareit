package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemsDifficileUsersException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.exception.UserNotExistsException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    @Override
    public ItemDto saveItem(ItemDto itemDto, Long id) {
        Optional<User> user = userRepository.findById(Long.valueOf(id));
        if (user.isPresent()) {
            Item item = ItemDtoMapper.mapToItem(itemDto, user.get());
            return ItemDtoMapper.mapToItemDto(itemRepository.save(item));
        }
        throw new UserNotExistsException();
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long sharedUserId) {
        Optional<Item> itemOp = itemRepository.findById(itemId);
        if (itemOp.isPresent()) {
            Item item = itemOp.get();
            if (sharedUserId.equals(item.getOwner())) {
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
            throw new ItemsDifficileUsersException();

        }
        throw new ItemNotFoundException();
    }

    @Override
    public ItemDto getItemById(Long id) {
        Optional<Item> item = itemRepository.findById(Long.valueOf(id));
        if (item.isPresent()) {
            return ItemDtoMapper.mapToItemDto(item.get());
        }
        throw new ItemNotFoundException();

    }

    @Override
    public List<ItemDto> getAllItemsForUser(Long userId) {
        return ItemDtoMapper.mapToItemDto(itemRepository.findByOwner(userId));
    }

    @Override
    public List<ItemDto> searchItemByText(String text) {
        if (!text.isBlank()) {
            return ItemDtoMapper.mapToItemDto(itemRepository.search(text));
        }
        return new ArrayList<>();
    }
}
