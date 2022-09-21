package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserNotExistsError;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserRepositoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private List<Item> items = new ArrayList<>();
    private int id = 1;
    private UserRepository userRepository;
    private ItemDtoMapper mapper;

    @Autowired
    public ItemRepositoryImpl(ItemDtoMapper mapper, UserRepositoryImpl userRepository){
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto saveItem(ItemDto itemDto, int sharedUserId) {
        itemDto.setId(getId());
        User user = userRepository.getUserById(sharedUserId);
        items.add(mapper.toItem(itemDto, user));
        return itemDto;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int id, int sharedUserId) {
        Item item = mapper.toItem(getItemById(id), userRepository.getUserById(sharedUserId));
        if(item.getId() == id){
            if(userRepository.getUserById(sharedUserId).equals(item.getOwner())){
                itemDto.setId(id);
                Item updateItem = mapper.toItem(itemDto, userRepository.getUserById(sharedUserId));
                if(updateItem.getName() == null){
                    updateItem.setName(item.getName());
                }
                if(updateItem.getDescription() == null){
                    updateItem.setDescription(item.getDescription());
                }
                if(updateItem.getAvailable() == null){
                    updateItem.setAvailable(item.getAvailable());
                }
                items.remove(item);
                items.add(updateItem);
                return mapper.toItemDto(updateItem);
            }
            throw new UserNotExistsError();
        }
        throw new ItemNotFoundError();
    }

    @Override
    public ItemDto getItemById(int id) {
        for(Item item: items){
            if(item.getId() == id){
                return mapper.toItemDto(item);
            }
        }
        throw new ItemNotFoundError();
    }

    @Override
    public List<ItemDto> getAllItemsForUser(int userId) {
        List<ItemDto> userItems = new ArrayList<>();
        for(Item item: items){
            if(item.getOwner().getId() == userId){
                userItems.add(mapper.toItemDto(item));
            }
        }
        return userItems;
    }

    @Override
    public List<ItemDto> searchItemByText(String text) {
        List<ItemDto> searchItems = new ArrayList<>();
        if(text.isBlank()){
            return searchItems;
        }
        for(Item item: items){
            if(item.getAvailable().equals("true")){
                if(item.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase())){
                    searchItems.add(mapper.toItemDto(item));
                } else if(item.getDescription().toLowerCase(Locale.ROOT).contains(text.toLowerCase())){
                    searchItems.add(mapper.toItemDto(item));
                }
            }
        }
        return searchItems;
    }

    private int getId() {
        id++;
        return id - 1;
    }
}
