package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserNotExistsError;

import java.util.List;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    ItemService itemService;

    @Autowired
    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable int itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsUser(@RequestHeader(value = "X-Sharer-User-Id") int sharedUserId) {
        return itemService.getAllItemsForUser(sharedUserId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByText(@RequestParam("text") String text) {
        return itemService.searchItemByText(text);
    }

    @PostMapping
    public ItemDto createItem(
            @RequestHeader(value = "X-Sharer-User-Id") int sharedUserId,
            @Validated @RequestBody ItemDto itemDto) {
        return itemService.saveItem(itemDto, sharedUserId);

    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(value = "X-Sharer-User-Id") int sharedUserId,
            @RequestBody ItemDto itemDto,
            @PathVariable int itemId) {
        return itemService.updateItem(itemDto, itemId, sharedUserId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Integer> handleUserNotFind(final UserNotExistsError e) {
        return Map.of("User error", 404);
    }
}
