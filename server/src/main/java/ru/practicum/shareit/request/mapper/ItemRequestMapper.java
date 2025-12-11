package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.Generated;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.SaveItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Generated
public class ItemRequestMapper {

    public static ItemRequestDto toRequestDto(ItemRequest itemRequest, List<ItemResponse> responses) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor() != null ? itemRequest.getRequestor().getId() : null,
                itemRequest.getCreated(),
                responses
        );
    }

    @Generated
    public static ItemRequest toRequest(SaveItemRequest saveItemRequest, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(saveItemRequest.getDescription());
        itemRequest.setRequestor(requestor);
        return itemRequest;
    }
}
