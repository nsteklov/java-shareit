package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final UserRepository userRepository;
    String error;

    public InMemoryItemRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Item save(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item newItem) {
        if (newItem.getId() == null) {
            error = "Id должен быть указан";
            throw new NotFoundException(error);
        }
        if (items.containsKey(newItem.getId())) {
            Item oldItem = items.get(newItem.getId());
            oldItem.setName(newItem.getName());
            oldItem.setDescription(newItem.getDescription());
            oldItem.setAvailable(newItem.isAvailable());
            items.put(oldItem.getId(), oldItem);
            return oldItem;
        }
        throw new NotFoundException("Вещь с id = " + newItem.getId() + " не найдена");
    }

    @Override
    public Optional<Item> findById(Long idOfItem) {
        Item item = items.keySet()
                .stream()
                .map(id -> Optional.ofNullable(items.get(idOfItem)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + idOfItem + " не найдена"));
        return Optional.ofNullable(item);
    }

    @Override
    public Collection<Item> findByUser(Long idOfUser) {
        if (!userRepository.existsById(idOfUser)) {
            throw new NotFoundException("Пользователь с id = " + idOfUser + " не найден");
        }

        User user = userRepository.findById(idOfUser).get();

        List<Item> itemsByUser = items.values().stream()
                .filter(currentItem -> currentItem.getOwner() == user)
                .collect(Collectors.toList());

        return itemsByUser;
    }

    @Override
    public Collection<Item> searchAvailable(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> foundItems = items.values().stream()
                .filter(item -> item.isAvailable() && (item.getName().toLowerCase().trim().contains(text.toLowerCase().trim()) || item.getDescription().toLowerCase().trim().contains(text.toLowerCase().trim())))
                .collect(Collectors.toList());

        return foundItems;
    }

    @Override
    public boolean existsById(Long id) {
        return items.keySet()
                .stream()
                .anyMatch(id::equals);
    }

    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
