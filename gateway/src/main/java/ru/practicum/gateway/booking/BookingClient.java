package ru.practicum.gateway.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
//import ru.practicum.gateway.booking.dto.BookingDto;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.HttpHeaders;
import ru.practicum.gateway.booking.dto.BookingDto;
import ru.practicum.gateway.booking.dto.SaveBookingRequest;
import ru.practicum.gateway.client.BaseClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Value("${server.host}")
    private String host;

    @Autowired
    public BookingClient(@Value("$server.host") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(SaveBookingRequest saveBookingRequest, Long bookerId) {
        return post(host + API_PREFIX, bookerId, saveBookingRequest);
    }

    public ResponseEntity<Object> approve(Long bookingId, boolean approved, Long userId) {
        Map<String, Object> parameters = Map.of(
                "approved",approved
        );
        return patch(host + API_PREFIX + "/" + bookingId + "?approved=" + approved, userId, parameters);
    }

    public ResponseEntity<Object> findById(Long bookingId, Long userId) {
        return get( host + API_PREFIX + "/" + bookingId, userId);
    }

    public ResponseEntity<Object> findByUserId(Long userId, String state) {
        Map<String, Object> parameters = Map.of(
                "state",state
        );
        if (state == null || state.isBlank()) {
            return get(host + API_PREFIX + "?state=ALL", userId, parameters);
        } else {
            return get(host + API_PREFIX + "?state=" + state, userId, parameters);
        }
    }

    public ResponseEntity<Object> findByOwnerId(Long ownerId, String state) {
        Map<String, Object> parameters = Map.of(
                "state",state
        );
        if (state == null || state.isBlank()) {
            return get(host + API_PREFIX + "/owner?state=ALL", ownerId, parameters);
        } else {
            return get(host + API_PREFIX + "/owner?state=" + state, ownerId, parameters);
        }
    }
}
