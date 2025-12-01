package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long idOfUser);

    ItemDto update(ItemDto itemDto, Long id, Long idOfUser);

    ItemDto findById(Long id);

    List<ItemDto> findByOwnerId(Long idOfUser);

    List<ItemDto> searchAvailable(String text);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);
}
