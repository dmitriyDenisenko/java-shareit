package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemsDifficileUsersException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.exception.UserNotExistsException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
public class ItemController {
    private ItemService itemService;

    @Autowired
    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsUser(@RequestHeader(value = "X-Sharer-User-Id") Long sharedUserId) {
        return itemService.getAllItemsForUser(sharedUserId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByText(@RequestParam("text") String text) {
        return itemService.searchItemByText(text);
    }

    @PostMapping
    public ItemDto createItem(
            @RequestHeader(value = "X-Sharer-User-Id") Long sharedUserId,
            @Validated @RequestBody ItemDto itemDto) {
        return itemService.saveItem(itemDto, sharedUserId);

    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(value = "X-Sharer-User-Id") Long sharedUserId,
            @RequestBody ItemDto itemDto,
            @PathVariable Long itemId) {
        return itemService.updateItem(itemDto, itemId, sharedUserId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Integer> handleUserNotFind(final UserNotExistsException e) {
        return Map.of("User error", 404);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Integer> handleItemUsers(final ItemsDifficileUsersException e) {
        return Map.of("Item user difficult", 404);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Integer> handleItem(final ItemNotFoundException e) {
        return Map.of("Item not found", 404);
    }
}
