package ru.practicum.gateway.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.booking.dto.SaveBookingRequest;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Value("${server.host}")
    private String host;

    @Autowired
    public UserClient(@Value("$server.host") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getAll() {
        return get(host + API_PREFIX);
    }

    public ResponseEntity<Object> create(UserDto userDto) {
        return post(host + API_PREFIX, userDto);
    }

    public ResponseEntity<Object> update(UserDto userDto, Long id) {
        return patch(host + API_PREFIX + "/" + id, userDto);
    }

    public ResponseEntity<Object> getUser(Long id) {
        return get(host + API_PREFIX + "/" + id);
    }

    public ResponseEntity<Object> deleteUser(Long id) {
        return delete(host + API_PREFIX + "/" + id);
    }
}
