package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Item update(Item item);

    Optional<Item> findById(Long id);

    Collection<Item> findByUser(Long idOfUser);

    public Collection<Item> searchAvailable(String text);

    boolean existsById(Long id);
}
