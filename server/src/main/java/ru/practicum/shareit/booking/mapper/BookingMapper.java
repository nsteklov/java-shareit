package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.Generated;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.SaveBookingRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    @Generated
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem() != null ? ItemMapper.toItemDtoWithoutComments(booking.getItem()) : null,
                booking.getBooker() != null ? UserMapper.toUserDto(booking.getBooker()) : null,
                booking.getStatus()
        );
    }

    public static Booking toBooking(SaveBookingRequest saveBookingRequest, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(saveBookingRequest.getStart());
        booking.setEnd(saveBookingRequest.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        return booking;
    }
}
