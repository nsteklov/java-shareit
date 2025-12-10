package ru.practicum.gateway.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.item.dto.CommentDto;
import ru.practicum.gateway.item.dto.ItemDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Value("${server.host}")
    private String host;

    @Autowired
    public ItemClient(@Value("$server.host") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(ItemDto itemDto, Long userId) {
        return post(host + API_PREFIX, userId, itemDto);
    }

    public ResponseEntity<Object> update(ItemDto itemDto, Long id, Long userId) {
        return patch(host + API_PREFIX + "/" + id, userId, itemDto);
    }

    public ResponseEntity<Object> findById(Long id) {
        return get(host + API_PREFIX + "/" + id);
    }

    public ResponseEntity<Object> findByOwnerId(Long ownerId) {
        return get(host + API_PREFIX, ownerId);
    }

    public ResponseEntity<Object> searchAvailable(String text) {
        Map<String, Object> parameters = Map.of(
                "text",text
        );
        if (text == null || text.isBlank()) {
            return get(host + API_PREFIX + "/search" + text, parameters);
        } else {
            return get(host + API_PREFIX + "/search?text=" + text, parameters);
        }
    }

    public ResponseEntity<Object> addComment(CommentDto commentDto, Long itemId, Long userId) {
        return post(host + API_PREFIX + "/" + itemId + "/comment", userId, commentDto);
    }
}
