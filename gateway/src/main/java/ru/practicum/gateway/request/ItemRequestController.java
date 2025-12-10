package ru.practicum.gateway.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.HttpHeaders;
import ru.practicum.gateway.exception.ValidationException;
import ru.practicum.gateway.request.dto.SaveItemRequest;

@RequestMapping(path = "/requests")
@RestController
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestclient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody SaveItemRequest saveItemRequest, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        if (saveItemRequest.getDescription() == null || saveItemRequest.getDescription().isBlank()) {
            throw new ValidationException("Описание запроса не должно быть пустым");
        }
        return itemRequestclient.create(saveItemRequest, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findByRequestorId(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return itemRequestclient.findByRequestorId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return itemRequestclient.findAll(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object>  findByIdAndOwnerId(@PathVariable Long id, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return itemRequestclient.findByIdAndOwnerId(id, userId);
    }
}
