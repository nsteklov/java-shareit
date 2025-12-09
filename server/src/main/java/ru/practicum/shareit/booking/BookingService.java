package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(SaveBookingRequest saveBookingRequest, Long bookerId);

    BookingDto approve(Long bookingId, boolean approved, Long userId);

    BookingDto findById(Long bookingId, Long userId);

    List<BookingDto> findByUserId(Long userId, String state);

    List<BookingDto> findByOwnerId(Long ownerId, String state);
}
