package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HttpHeaders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.SaveItemRequest;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto create(@RequestBody SaveItemRequest saveItemRequest, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return itemRequestService.create(saveItemRequest, userId);
    }

    @GetMapping
    public List<ItemRequestDto> findByRequestorId(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return itemRequestService.findByRequestorId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return itemRequestService.findAll(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDto findByIdAndOwnerId(@PathVariable Long id, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return itemRequestService.findByIdAndOwnerId(id, userId);
    }
}
