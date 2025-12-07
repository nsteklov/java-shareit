package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HttpHeaders;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestBody SaveBookingRequest saveBookingRequest, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long bookerId) {
        return bookingService.create(saveBookingRequest, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable Long bookingId, @RequestParam boolean approved, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return bookingService.approve(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable Long bookingId, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findByUserId(@RequestParam(defaultValue = "ALL") String state, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long userId) {
        return bookingService.findByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findByOwnerId(@RequestParam(defaultValue = "ALL") String state, @RequestHeader(HttpHeaders.X_SHARER_USER_ID) Long ownerId) {
        return bookingService.findByOwnerId(ownerId, state);
    }
}
