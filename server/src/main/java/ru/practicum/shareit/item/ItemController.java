package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HttpHeaders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestBody ItemDto itemDto, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long id, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return itemService.update(itemDto, id, userId);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable Long id) {
        return itemService.findById(id);
    }

    @GetMapping()
    public List<ItemDto> findByUser(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return itemService.findByOwnerId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailable(@RequestParam(required = false) String text) {
        System.out.println(text);
        return itemService.searchAvailable(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto create(@RequestBody CommentDto commentDto, @PathVariable Long itemId, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return itemService.addComment(commentDto, itemId, userId);
    }
}
