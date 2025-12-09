package ru.practicum.gateway.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import ru.practicum.gateway.booking.dto.BookingDto;
import ru.practicum.gateway.booking.dto.SaveBookingRequest;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.HttpHeaders;
import ru.practicum.gateway.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping(path = "/bookings")
@RestController
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody SaveBookingRequest saveBookingRequest, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long bookerId) {
        if (saveBookingRequest.getStart() == null) {
            throw new ValidationException("Не указана дата начала бронирования");
        }
        if (saveBookingRequest.getEnd() == null) {
            throw new ValidationException("Не указана дата окончания бронирования");
        }
        if (saveBookingRequest.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала бронирования меньше текущей даты");
        }
        if (saveBookingRequest.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата окончания бронирования меньше текущей даты");
        }
        if (saveBookingRequest.getStart().equals(saveBookingRequest.getEnd())) {
            throw new ValidationException("Даты начала и окончания бронирования совпадают");
        }
        if (saveBookingRequest.getEnd().isBefore(saveBookingRequest.getStart())) {
            throw new ValidationException("Дата окончания бронирования меньше даты начала бронирования");
        }
        return client.create(saveBookingRequest, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@PathVariable Long bookingId, @RequestParam boolean approved, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return client.approve(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public  ResponseEntity<Object> findById(@PathVariable Long bookingId, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return client.findById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findByUserId(@RequestParam(defaultValue = "ALL") String state, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return client.findByUserId(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByOwnerId(@RequestParam(defaultValue = "ALL") String state, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long ownerId) {
        return client.findByOwnerId(ownerId, state);
    }
}
