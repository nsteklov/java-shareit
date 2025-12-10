package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.SaveBookingRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {
    private final JacksonTester<SaveBookingRequest> json;

    @Test
    void testBookingDto() throws Exception {
        SaveBookingRequest bookingDto = new SaveBookingRequest();
        bookingDto.setStart(LocalDateTime.of(2026, 12, 31, 13, 45, 10));
        bookingDto.setEnd(LocalDateTime.of(2028, 12, 31, 13, 45, 10));
        bookingDto.setItemId(1L);

        JsonContent<SaveBookingRequest> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(LocalDateTime.of(2026, 12, 31, 13, 45, 10).toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(LocalDateTime.of(2028, 12, 31, 13, 45, 10).toString());
    }
}