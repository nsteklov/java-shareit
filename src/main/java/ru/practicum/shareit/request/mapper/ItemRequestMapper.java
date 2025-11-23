package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequestDto toBookingDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getDescription(),
                itemRequest.getRequestor() != null ? itemRequest.getRequestor().getId() : null,
                itemRequest.getCreated()
        );
    }
}
