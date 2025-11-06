package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.update(itemDto, id, userId);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable Long id) {
        return itemService.findById(id);
    }

    @GetMapping()
    public Collection<ItemDto> findByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findByUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchAvailable(@RequestParam(required = false) String text) {
        return itemService.searchAvailable(text);
    }
}
