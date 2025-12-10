package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.SaveBookingRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoJsonTest {
    private final JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("патефон");
        itemDto.setDescription("крутой патефон");

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("патефон");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("крутой патефон");
    }
}