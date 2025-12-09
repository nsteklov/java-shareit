package ru.practicum.gateway.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.HttpHeaders;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.item.dto.CommentDto;
import ru.practicum.gateway.request.dto.SaveItemRequest;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Value("${server.host}")
    private String host;

    @Autowired
    public ItemRequestClient(@Value("$server.host") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(SaveItemRequest saveItemRequest, Long userId) {
        return post(host + API_PREFIX, userId, saveItemRequest);
    }

    public ResponseEntity<Object> findByRequestorId(Long requestorId) {
        return get(host + API_PREFIX, requestorId);
    }

    public ResponseEntity<Object> findAll(Long userId) {
        return get(host + API_PREFIX + "/all", userId);
    }

    public ResponseEntity<Object> findByIdAndOwnerId(Long id, Long userId) {
        return get(host + API_PREFIX + "/" +  id, userId);
    }
}
