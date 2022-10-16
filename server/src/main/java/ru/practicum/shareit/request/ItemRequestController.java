package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    RequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long sharerUserId,
                             @RequestBody RequestDto requestDto) {
        log.info("Created request. User_id: {}, Request_id: {}", sharerUserId, requestDto.getId());
        return itemRequestService.createRequest(sharerUserId, requestDto);
    }

    @GetMapping("/all")
    List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long sharerUserId,
                                @RequestParam(defaultValue = "1") Integer from,
                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get requests all. User_id {}, From: {}, Size: {}", sharerUserId, from, size);
        return itemRequestService.getAll(sharerUserId, from, size);
    }

    @GetMapping
    List<ItemRequestDto> getByRequester(@RequestHeader("X-Sharer-User-Id") Long sharerUserId) {
        log.info("Get requests by User: {}", sharerUserId);
        return itemRequestService.getByRequester(sharerUserId);
    }

    @GetMapping("/{requestId}")
    ItemRequestDto getById(@RequestHeader("X-Sharer-User-Id") Long sharerUserId,
                           @PathVariable Long requestId) {
        log.info("Get request by id: {}", requestId);
        return itemRequestService.getById(sharerUserId, requestId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Integer> handleNotFoundItemRequest(final ItemRequestNotFoundException e) {
        log.warn("The action was not completed successfully");
        return Map.of(e.getMessage(), 404);
    }
}
