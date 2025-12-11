package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SaveBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private final EntityManager em;
    private final BookingService service;
    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    @Autowired
    private BookingService bookingService;

    @Test
    void findByUserId() {

        UserDto userDto1 = makeUserDto(null, "vasya1", "vasy1a@mail.ru");
        UserDto userDto2 = makeUserDto(null, "petya1", "petya1@mail.ru");
        User user1 = UserMapper.toUser(userDto1);
        User user2 = UserMapper.toUser(userDto2);

        User savedUser1  = userRepository.save(user1);
        User savedUser2  = userRepository.save(user2);

        ItemDto itemDto1 = makeItemDto(null, "патефон1", "крутой патефон",true);
        ItemDto itemDto2 = makeItemDto(null, "граммофон1", "крутой патефон",true);
        Item item1 = ItemMapper.toItem(itemDto1, savedUser1);
        Item item2 = ItemMapper.toItem(itemDto2, savedUser1);
        Item savedItem1 = itemRepository.save(item1);
        Item savedItem2 = itemRepository.save(item2);

        SaveBookingRequest saveBookingRequest1 = makeSaveBookingRequest(LocalDateTime.of(2026, 12, 31, 13, 45, 10), LocalDateTime.of(2028, 12, 31, 13, 45, 10), savedItem1.getId());
        SaveBookingRequest saveBookingRequest2 = makeSaveBookingRequest(LocalDateTime.of(2027, 12, 31, 13, 45, 10), LocalDateTime.of(2029, 12, 31, 13, 45, 10), savedItem2.getId());

        List<SaveBookingRequest> sourceBookings = List.of(
                saveBookingRequest1,
                saveBookingRequest2);

        Booking entity1 = BookingMapper.toBooking(saveBookingRequest1, item1, savedUser2);
        entity1.setStatus(Status.WAITING);
        em.persist(entity1);

        Booking entity2 = BookingMapper.toBooking(saveBookingRequest2, item2, savedUser2);
        entity2.setStatus(Status.WAITING);
        em.persist(entity2);

        em.flush();

        Collection<BookingDto> targetBookings = service.findByUserId(savedUser2.getId(), "WAITING");

        assertThat(targetBookings, hasSize(sourceBookings.size()));
        for (SaveBookingRequest sourceBooking : sourceBookings) {
            assertThat(targetBookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("start", equalTo(sourceBooking.getStart())),
                    hasProperty("end", equalTo(sourceBooking.getEnd()))
            )));
        }
        targetBookings = service.findByUserId(savedUser2.getId(), "ALL");
        assertThat(targetBookings, hasSize(sourceBookings.size()));
        for (SaveBookingRequest sourceBooking2 : sourceBookings) {
            assertThat(targetBookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("start", equalTo(sourceBooking2.getStart())),
                    hasProperty("end", equalTo(sourceBooking2.getEnd()))
            )));
        }

        targetBookings = service.findByUserId(savedUser2.getId(), "CURRENT");
        assertThat(targetBookings, hasSize(0));

        targetBookings = service.findByUserId(savedUser2.getId(), "PAST");
        assertThat(targetBookings, hasSize(0));

        targetBookings = service.findByUserId(savedUser2.getId(), "FUTURE");
        assertThat(targetBookings, hasSize(0));

        targetBookings = service.findByUserId(savedUser2.getId(), "REJECTED");
        assertThat(targetBookings, hasSize(0));

    }

    @Test
    void saveBooking() {

        UserDto userDto1 = makeUserDto(null, "vasya", "vasya@mail.ru");
        UserDto userDto2 = makeUserDto(null, "petya", "petya@mail.ru");
        User user1 = UserMapper.toUser(userDto1);
        User user2 = UserMapper.toUser(userDto2);

        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        ItemDto itemDto1 = makeItemDto(null, "патефон", "крутой патефон",true);
        Item item1 = ItemMapper.toItem(itemDto1, savedUser1);

        Item savedItem1 = itemRepository.save(item1);

        SaveBookingRequest saveBookingRequest1 = makeSaveBookingRequest(LocalDateTime.of(2026, 12, 31, 13, 45, 10), LocalDateTime.of(2028, 12, 31, 13, 45, 10), savedItem1.getId());

        BookingDto bookingDto = bookingService.create(saveBookingRequest1, savedUser2.getId());

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingDto.getId())
                .getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(saveBookingRequest1.getStart()));
        assertThat(booking.getEnd(), equalTo(saveBookingRequest1.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(saveBookingRequest1.getItemId()));
    }

    @Test
    void approveByNotOwner() {

        UserDto userDto1 = makeUserDto(null, "vasya32", "vasya32@mail.ru");
        UserDto userDto2 = makeUserDto(null, "petya44", "petya44@mail.ru");
        User user1 = UserMapper.toUser(userDto1);
        User user2 = UserMapper.toUser(userDto2);

        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        ItemDto itemDto1 = makeItemDto(null, "патефон11", "крутой патефон",true);
        Item item1 = ItemMapper.toItem(itemDto1, savedUser1);

        Item savedItem1 = itemRepository.save(item1);

        SaveBookingRequest saveBookingRequest1 = makeSaveBookingRequest(LocalDateTime.of(2026, 12, 31, 13, 45, 10), LocalDateTime.of(2028, 12, 31, 13, 45, 10), savedItem1.getId());
        BookingDto bookingDto = bookingService.create(saveBookingRequest1, savedUser2.getId());
        assertThrows(ValidationException.class, () -> bookingService.approve(bookingDto.getId(), true, savedUser2.getId()));
    }

    @Test
    void findByIncorrectId() {

        UserDto userDto1 = makeUserDto(null, "vasya", "vasya@mail.ru");
        UserDto userDto2 = makeUserDto(null, "petya", "petya@mail.ru");
        User user1 = UserMapper.toUser(userDto1);
        User user2 = UserMapper.toUser(userDto2);

        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        ItemDto itemDto1 = makeItemDto(null, "патефон", "крутой патефон",true);
        Item item1 = ItemMapper.toItem(itemDto1, savedUser1);
        Item savedItem1 = itemRepository.save(item1);

        SaveBookingRequest saveBookingRequest1 = makeSaveBookingRequest(LocalDateTime.of(2026, 12, 31, 13, 45, 10), LocalDateTime.of(2028, 12, 31, 13, 45, 10), savedItem1.getId());
        BookingDto bookingDto = bookingService.create(saveBookingRequest1, savedUser2.getId());

        assertThrows(NotFoundException.class, () -> bookingService.findById(11L, savedUser1.getId()));
    }

    @Test
    void findByOwnerId() {

        UserDto userDto1 = makeUserDto(null, "vasya1", "vasy1a@mail.ru");
        UserDto userDto2 = makeUserDto(null, "petya1", "petya1@mail.ru");
        User user1 = UserMapper.toUser(userDto1);
        User user2 = UserMapper.toUser(userDto2);

        User savedUser1  = userRepository.save(user1);
        User savedUser2  = userRepository.save(user2);

        ItemDto itemDto1 = makeItemDto(null, "патефон1", "крутой патефон",true);
        ItemDto itemDto2 = makeItemDto(null, "граммофон1", "крутой патефон",true);
        Item item1 = ItemMapper.toItem(itemDto1, savedUser1);
        Item item2 = ItemMapper.toItem(itemDto2, savedUser1);
        Item savedItem1 = itemRepository.save(item1);
        Item savedItem2 = itemRepository.save(item2);

        SaveBookingRequest saveBookingRequest1 = makeSaveBookingRequest(LocalDateTime.of(2026, 12, 31, 13, 45, 10), LocalDateTime.of(2028, 12, 31, 13, 45, 10), savedItem1.getId());
        SaveBookingRequest saveBookingRequest2 = makeSaveBookingRequest(LocalDateTime.of(2027, 12, 31, 13, 45, 10), LocalDateTime.of(2029, 12, 31, 13, 45, 10), savedItem2.getId());

        List<SaveBookingRequest> sourceBookings = List.of(
                saveBookingRequest1,
                saveBookingRequest2);

        Booking entity1 = BookingMapper.toBooking(saveBookingRequest1, item1, savedUser2);
        entity1.setStatus(Status.WAITING);
        em.persist(entity1);

        Booking entity2 = BookingMapper.toBooking(saveBookingRequest2, item2, savedUser2);
        entity2.setStatus(Status.WAITING);
        em.persist(entity2);

        em.flush();

        Collection<BookingDto> targetBookings = service.findByOwnerId(savedUser1.getId(), "WAITING");

        assertThat(targetBookings, hasSize(sourceBookings.size()));
        for (SaveBookingRequest sourceBooking : sourceBookings) {
            assertThat(targetBookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("start", equalTo(sourceBooking.getStart())),
                    hasProperty("end", equalTo(sourceBooking.getEnd()))
            )));
        }
    }

    private SaveBookingRequest makeSaveBookingRequest(LocalDateTime start, LocalDateTime end, Long itemId) {
        SaveBookingRequest saveBookingRequest = new SaveBookingRequest();
        saveBookingRequest.setStart(start);
        saveBookingRequest.setEnd(end);
        saveBookingRequest.setItemId(itemId);

        return saveBookingRequest;
    }

    private UserDto makeUserDto(Long id, String name, String email) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);

        return dto;
    }

    private ItemDto makeItemDto(Long id, String name, String description, Boolean available) {
        ItemDto dto = new ItemDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);

        return dto;
    }
}