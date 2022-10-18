package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemsDifficileUsersException;
import ru.practicum.shareit.item.exception.UserIsNotOwnerException;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.user.exception.UserNotExistsException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.item.ItemTestData.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    private final ItemServiceImpl itemService;

    @Test
    @DirtiesContext
    void testCreate() {
        long itemId = itemService.saveItem(itemDtoCreated, 1L).getId();
        ItemDto itemDtoFromSQL = itemService.getItemById(1L, itemId);
        assertThat(itemDtoFromSQL, equalTo(itemDtoCreated));
    }

    @Test
    @DirtiesContext
    void testCreateWithItemRequest() {
        itemDtoCreated.setRequestId(1L);
        long itemId = itemService.saveItem(itemDtoCreated, 1L).getId();
        ItemDto itemDtoFromSQL = itemService.getItemById(1L, itemId);
        assertThat(itemDtoFromSQL, equalTo(itemDtoCreated));
        itemDtoCreated.setRequestId(null);
    }

    @Test
    void testCreateWithWrongItemRequest() {
        itemDtoCreated.setRequestId(10L);
        assertThrows(ItemRequestNotFoundException.class, () -> itemService.saveItem(itemDtoCreated, 1L));
        itemDtoCreated.setRequestId(null);
    }

    @Test
    @DirtiesContext
    void testUpdate() {
        itemDto1.setName("new name");
        itemService.updateItem(itemDto1, 1L, 1L);
        ItemDto itemDtoFromSQL = itemService.getItemById(1L, 1L);
        assertThat(itemDtoFromSQL.getName(), equalTo(itemDto1.getName()));
        itemDto1.setName("item1");
    }

    @Test
    @DirtiesContext
    void testUpdateWithNulls() {
        itemDto1.setName(null);
        itemDto1.setDescription(null);
        itemDto1.setAvailable(null);
        itemService.updateItem(itemDto1, 1L, 1L);
        ItemDto itemDtoFromSQL = itemService.getItemById(1L, 1L);
        itemDto1.setName("item1");
        itemDto1.setDescription("description1");
        itemDto1.setAvailable(true);
        assertThat(itemDtoFromSQL.getName(), equalTo(itemDto1.getName()));
    }

    @Test
    @DirtiesContext
    void testUpdateDescription() {
        itemDto1.setDescription("new description");
        itemService.updateItem(itemDto1, 1L, 1L);
        ItemDto itemDtoFromSQL = itemService.getItemById(1L, 1L);
        assertThat(itemDtoFromSQL.getName(), equalTo(itemDto1.getName()));
        itemDto1.setDescription("description1");
    }

    @Test
    @DirtiesContext
    void testUpdateAvailable() {
        itemDto1.setAvailable(false);
        itemService.updateItem(itemDto1, 1L, 1L);
        ItemDto itemDtoFromSQL = itemService.getItemById(1L, 1L);
        assertThat(itemDtoFromSQL.getName(), equalTo(itemDto1.getName()));
        itemDto1.setAvailable(true);
    }

    @Test
    void testUpdateWrongItem() {
        itemDto1.setName("new name");
        assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(itemDto1, 50L, 1L));
        itemDto1.setName("item1");
    }

    @Test
    void testUpdateWrongOwner() {
        itemService.saveItem(itemDto1, 1L);
        itemDto1.setName("new name");
        assertThrows(ItemsDifficileUsersException.class, () -> itemService.updateItem(itemDto1, 1L,
                10L));
        itemDto1.setName("item1");
    }

    @Test
    void testUpdateNotOwner() {
        assertThrows(ItemsDifficileUsersException.class, () ->
                itemService.updateItem(itemDto1, 1L, 2L));
    }

    @Test
    void testGetItemById() {
        ItemDto itemDtoFromSQL = itemService.getItemById(2L, 2L);
        assertThat(itemDtoFromSQL, equalTo(itemDto2));
    }

    @Test
    void testGetItemByWrongId() {
        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(1L,
                10L));
    }

    @Test
    void testGetAllItemsByUser() {
        List<ItemDto> items = itemService.getAllItemsForUser(1, 10, 2L);
        assertThat(items, equalTo(List.of(itemDto4)));
    }

    @Test
    void testGetAllItemsByWrongUser() {
        assertThrows(UserNotExistsException.class, () -> itemService.getAllItemsForUser(1, 10, 20L));
    }

    @Test
    void testSearch() {
        itemDto1.setComments(null);
        List<ItemDto> items = itemService.searchItemByText(1, 10, "item1");
        assertThat(items, equalTo(List.of(itemDto1)));
        itemDto1.setComments(new ArrayList<>());
    }

    @Test
    void testSearchEmpty() {
        List<ItemDto> items = itemService.searchItemByText(1, 10, "");
        assertThat(items, equalTo(new ArrayList<>()));
    }

    @Test
    void testCreateItemWrongOwner() {
        assertThrows(UserNotExistsException.class, () ->
                itemService.saveItem(itemDto1, 500L));
    }

    @Test
    void testCreateEmptyComment() {
        commentDto.setText("");
        assertThrows(UserIsNotOwnerException.class, () ->
                itemService.postComment(2L, 1L, commentDto));
        commentDto.setText("comment");
    }

    @Test
    void testCreateCommentWrongItem() {
        assertThrows(ItemNotFoundException.class, () ->
                itemService.postComment(2L, 10L, commentDto));
    }

    @Test
    void testCreateCommentWrongBooking() {
        assertThrows(UserIsNotOwnerException.class, () ->
                itemService.postComment(3L, 1L, commentDto));
    }

    @Test
    void testCreateCommentWrongUser() {
        assertThrows(UserNotExistsException.class, () ->
                itemService.postComment(300L, 1L, commentDto));
    }

    @Test
    void testCreateCommentWrongBookingUser() {
        assertThrows(UserIsNotOwnerException.class, () ->
                itemService.postComment(1L, 1L, commentDto));
    }

    @Test
    @DirtiesContext
    void testCreateComment() {
        CommentDto commentDto1 = itemService.postComment(2L, 1L, commentDto);
        assertThat(commentDto1.getId(), equalTo(commentDto.getId()));
    }

    @Test
    void testCreateCommentNotBooker() {
        assertThrows(UserIsNotOwnerException.class, () -> itemService.postComment(3L, 1L, commentDto));
    }

    @Test
    void testFindCommentsByWrongItem() {
        assertThrows(ItemNotFoundException.class, () -> itemService.findCommentsByItem(34L));
    }

}