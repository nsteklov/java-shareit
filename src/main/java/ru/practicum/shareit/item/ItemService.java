package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long idOfUser);

    ItemDto update(ItemDto itemDto, Long id, Long idOfUser);

    ItemDto findById(Long id);

    Collection<ItemDto> findByUser(Long idOfUser);

    public Collection<ItemDto> searchAvailable(String text);
}
