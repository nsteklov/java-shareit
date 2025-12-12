package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.SaveItemRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestJsonTest {
    private final JacksonTester<SaveItemRequest> json;

    @Test
    void testItemRequestDto() throws Exception {
        SaveItemRequest saveItemRequest = new SaveItemRequest();
        saveItemRequest.setDescription("Запрос 1");

        JsonContent<SaveItemRequest> result = json.write(saveItemRequest);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Запрос 1");
    }
}