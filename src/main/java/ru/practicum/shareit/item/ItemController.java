package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemsDifficileUsersException;
import ru.practicum.shareit.item.exception.UserIsNotOwnerException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.exception.UserNotExistsException;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
    ItemDto getItemById(@NotBlank @RequestHeader("X-Sharer-User-Id") long userId,
                        @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
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

    @PostMapping("/{itemId}/comment")
    CommentDto createComment(@NotBlank @RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable long itemId,
                             @RequestBody @Valid CommentDto commentDto) {
        return itemService.postComment(userId, itemId, commentDto);
    }

    @ExceptionHandler(value = {UserNotExistsException.class, ItemsDifficileUsersException.class,
            ItemNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Integer> handleItemsProblems(final RuntimeException e) {
        return Map.of(e.getMessage(), 404);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Integer> handleCommentPost(final UserIsNotOwnerException e) {
        return Map.of(e.getMessage(), 400);
    }

}
