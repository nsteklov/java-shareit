package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithoutComments;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item, LocalDateTime lastBooking,  LocalDateTime nextBooking, List<CommentDto> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequestId() != null ? item.getRequestId() : null,
                lastBooking,
                nextBooking,
                comments
        );
    }

    public static ItemDtoWithoutComments toItemDtoWithoutComments(Item item) {
        return new ItemDtoWithoutComments(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequestId() != null ? item.getRequestId() : null
        );
    }

    public static ItemResponse toItemResponse(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getOwner().getId()
        );
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() == null) {
            item.setAvailable(false);
        } else {
            item.setAvailable(itemDto.getAvailable());
        }
        item.setOwner(owner);
        item.setRequestId(itemDto.getRequestId());
        return item;
    }
}
