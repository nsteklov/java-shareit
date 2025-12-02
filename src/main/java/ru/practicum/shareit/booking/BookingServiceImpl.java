package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    String error;

    @Override
    public BookingDto create(SaveBookingRequest saveBookingRequest, Long bookerId) {
        Item item = itemRepository.findById(saveBookingRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь, осуществляющий бронирование, не найден"));
        if (!item.isAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (saveBookingRequest.getStart() == null) {
            throw new ValidationException("Не указана дата начала бронирования");
        }
        if (saveBookingRequest.getEnd() == null) {
            throw new ValidationException("Не указана дата окончания бронирования");
        }
        if (saveBookingRequest.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала бронирования меньше текущей даты");
        }
        if (saveBookingRequest.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата окончания бронирования меньше текущей даты");
        }
        if (saveBookingRequest.getStart().equals(saveBookingRequest.getEnd())) {
            throw new ValidationException("Даты начала и окончания бронирования совпадают");
        }
        if (saveBookingRequest.getEnd().isBefore(saveBookingRequest.getStart())) {
            throw new ValidationException("Дата окончания бронирования меньше даты начала бронирования");
        }
        Booking booking = BookingMapper.toBooking(saveBookingRequest, item, booker);
        booking.setStatus(Status.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto approve(Long bookingId, boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            error = "Статус бронирования может изменять только владелец вещи";
            throw new ValidationException(error);
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto findById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            error = "Получать данные бронирование может только пользователь, осуществляющий бронирование, и владелец вещи";
            throw new ValidationException(error);
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findByUserId(Long userId, String state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (state.equals("ALL")) {
            return bookingRepository.findByBookerIdAll(userId).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals("CURRENT")) {
            return bookingRepository.findByBookerIdCurrent(userId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals("PAST")) {
            return bookingRepository.findByBookerIdPast(userId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals("FUTURE")) {
            return bookingRepository.findByBookerIdFuture(userId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals("WAITING")) {
            return bookingRepository.findByBookerIdWaiting(userId).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals("REJECTED")) {
            return bookingRepository.findByBookerIdRejected(userId).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        ;
        return new ArrayList<>();
    }

    @Override
    public List<BookingDto> findByOwnerId(Long ownerId, String state) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (state.equals("ALL")) {
            return bookingRepository.findByOwnerIdAll(ownerId).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals("CURRENT")) {
            return bookingRepository.findByOwnerIdCurrent(ownerId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals("PAST")) {
            return bookingRepository.findByOwnerIdPast(ownerId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals("FUTURE")) {
            return bookingRepository.findByOwnerIdFuture(ownerId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals("WAITING")) {
            return bookingRepository.findByOwnerIdWaiting(ownerId).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals("REJECTED")) {
            return bookingRepository.findByOwnerIdRejected(ownerId).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        ;
        return new ArrayList<>();
    }
}
