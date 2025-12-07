package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SaveBookingRequest {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
