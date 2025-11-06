package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    String error;

    @Override
    public ItemDto create(ItemDto itemDto, Long idOfUser) {
        if (!userRepository.existsById(idOfUser)) {
            throw new NotFoundException("Пользователь с id = " + idOfUser + " не найден");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Должна быть указана доступность вещи");
        }
        User user = userRepository.findById(idOfUser).get();
        Item item = ItemMapper.toItem(itemDto, user);
        validate(item);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long id, Long idOfUser) {
        if (!userRepository.existsById(idOfUser)) {
            throw new NotFoundException("Пользователь с id = " + idOfUser + " не найден");
        }
        if (!itemRepository.existsById(id)) {
            throw new NotFoundException("Вещь с id = " + id + " не найдена");
        }
        User user = userRepository.findById(idOfUser).get();
        Item oldItem = itemRepository.findById(id).get();
        if (oldItem.getOwner() != user) {
            throw new ValidationException("Вещь может редактировать только её владелец");
        }
        Item item = ItemMapper.toItem(itemDto, user);
        item.setId(id);
        if (itemDto.getName() == null) {
            item.setName(oldItem.getName());
        }
        if (itemDto.getDescription() == null) {
            item.setDescription(oldItem.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            item.setAvailable(oldItem.isAvailable());
        }
        validate(item);
        Item updatedItem = itemRepository.update(item);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto findById(Long id) {
        return itemRepository.findById(id)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + id + " не найдена"));
    }

    @Override
    public Collection<ItemDto> findByUser(Long idOfUser) {
        return itemRepository.findByUser(idOfUser).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchAvailable(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.searchAvailable(text.toLowerCase().trim()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validate(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            error = "Наименование вещи не может быть пустым";
            throw new ValidationException(error);
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            error = "Описание вещи не может быть пустым";
            throw new ValidationException(error);
        }
    }
}
