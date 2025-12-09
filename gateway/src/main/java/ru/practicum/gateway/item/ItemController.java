package ru.practicum.gateway.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.exception.ValidationException;
import ru.practicum.gateway.item.dto.CommentDto;
import ru.practicum.gateway.item.dto.ItemDto;
import ru.practicum.gateway.HttpHeaders;

@RequestMapping(path = "/items")
@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody ItemDto itemDto, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Должна быть указана доступность вещи");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Наименование вещи не может быть пустым");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не может быть пустым");
        }
        return client.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody ItemDto itemDto, @PathVariable Long id, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        if (itemDto.getName() != null && itemDto.getName().isBlank()) {
            throw new ValidationException("Наименование вещи не может быть пустым");
        }
        if (itemDto.getDescription() != null && itemDto.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не может быть пустым");
        }
        return client.update(itemDto, id, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        return client.findById(id);
    }

    @GetMapping
    public ResponseEntity<Object> findByUser(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return client.findByOwnerId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchAvailable(@RequestParam(required = false) String text) {
        return client.searchAvailable(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody CommentDto commentDto, @PathVariable Long itemId, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return client.addComment(commentDto, itemId, userId);
    }
}
