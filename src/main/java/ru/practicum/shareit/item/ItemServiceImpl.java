package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
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
        return ItemMapper.toItemDto(savedItem, null, null, new ArrayList<>());
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
        Item updatedItem = itemRepository.save(item);
        List<CommentDto> commentsDto = commentRepository.findByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        return ItemMapper.toItemDto(updatedItem, null, null, commentsDto);
    }

    @Override
    public ItemDto findById(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new NotFoundException("Вещь с id = " + id + " не найдена");
        }
        Item item = itemRepository.findById(id).get();
        List<CommentDto> commentsDto = commentRepository.findByItemId(id).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        LocalDateTime lastBooking = null;
        LocalDateTime nextBooking = null;
        List<Booking> bookings = bookingRepository.findByItemId(item.getId());
        Optional<LocalDateTime> optNextBooking = bookings.stream()
                .map(Booking::getStart)
                .filter(start -> start.isAfter(LocalDateTime.now()))
                .min(Comparator.naturalOrder());
        if (optNextBooking.isPresent()) {
            nextBooking = optNextBooking.get();
        }
        Optional<LocalDateTime> optLastBooking = bookings.stream()
                .map(Booking::getStart)
                .filter(start -> start.isBefore(LocalDateTime.now()))
                .max(Comparator.naturalOrder());
        if (optLastBooking.isPresent()) {
            lastBooking = optLastBooking.get();
        }
        return ItemMapper.toItemDto(item, null, null, commentsDto);
    }

    @Override
    public List<ItemDto> findByOwnerId(Long idOfOwner) {
        List<Item> itemsByOwner = itemRepository.findByOwnerId(idOfOwner);
        List<ItemDto> itemsByOwnerExtendedDto = new ArrayList<>();
        LocalDateTime lastBooking;
        LocalDateTime nextBooking;
        for (Item item : itemsByOwner) {
            List<Booking> bookingsByOwner = bookingRepository.findByItemId(item.getId());
            Optional<LocalDateTime> optNextBooking = bookingsByOwner.stream()
                    .map(Booking::getStart)
                    .filter(start -> start.isAfter(LocalDateTime.now()))
                    .min(Comparator.naturalOrder());
            if (optNextBooking.isPresent()) {
                nextBooking = optNextBooking.get();
            } else {
                nextBooking = null;
            }
            Optional<LocalDateTime> optLastBooking = bookingsByOwner.stream()
                    .map(Booking::getStart)
                    .filter(start -> start.isBefore(LocalDateTime.now()))
                    .max(Comparator.naturalOrder());
            if (optLastBooking.isPresent()) {
                lastBooking = optLastBooking.get();
            } else {
                lastBooking = null;
            }
            List<CommentDto> commentsDto = commentRepository.findByItemId(item.getId()).stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
            itemsByOwnerExtendedDto.add(ItemMapper.toItemDto(item, lastBooking, nextBooking, commentsDto));
        }
        return itemsByOwnerExtendedDto;
    }

    @Override
    public List<ItemDto> searchAvailable(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findAvailable(text.toLowerCase().trim()).stream()
                .map(item -> ItemMapper.toItemDto(item, null, null, commentRepository.findByItemId(item.getId()).stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Вещь с id = " + itemId + " не найдена");
        }
        Item item = itemRepository.findById(itemId).get();
        List<Booking> bookings = bookingRepository.findByItemId(itemId).stream()
                .filter(booking -> booking.getBooker().getId().equals(userId)
                        && booking.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new ValidationException("Пользователь с id = " + userId + " не брал в аренду вещь с id " + itemId);
        }
        Comment comment = CommentMapper.toComment(commentDto, item, userRepository.findById(userId).get());
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
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
