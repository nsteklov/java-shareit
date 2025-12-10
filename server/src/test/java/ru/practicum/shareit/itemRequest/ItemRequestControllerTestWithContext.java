package ru.practicum.shareit.itemRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.SaveItemRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTestWithContext {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService requestService;

    @Autowired
    private MockMvc mvc;


    @Test
    void saveNewRequest() throws Exception {
        Long userId = 2L;
        Long itemRequestId = 3L;

        SaveItemRequest saveItemRequest = makeItemRequestDto("Запрос 1");

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequestId);
        itemRequestDto.setDescription(saveItemRequest.getDescription());
        itemRequestDto.setRequestorId(userId);
        itemRequestDto.setCreated(LocalDateTime.of(2026, 12, 31, 13, 45, 10));

        when(requestService.create(saveItemRequest, userId))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(saveItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestorId", is(2)))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())));
    }

    @Test
    void findByRequestorId() throws Exception {

        Long userId = 2L;
        Long requestId1 = 3L;
        Long requestId2 = 4L;

        SaveItemRequest saveItemRequest1 = makeItemRequestDto("Запрос 1");
        SaveItemRequest saveItemRequest2 = makeItemRequestDto("Запрос 2");

        ItemRequestDto itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setId(requestId1);
        itemRequestDto1.setDescription(saveItemRequest1.getDescription());
        itemRequestDto1.setRequestorId(userId);
        itemRequestDto1.setCreated(LocalDateTime.of(2026, 12, 31, 13, 45, 10));

        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setId(requestId2);
        itemRequestDto2.setDescription(saveItemRequest2.getDescription());
        itemRequestDto2.setRequestorId(userId);
        itemRequestDto2.setCreated(LocalDateTime.of(2027, 12, 31, 13, 45, 10));

        List<ItemRequestDto> requestDtoList = new ArrayList<>(List.of(itemRequestDto1, itemRequestDto2));

        when(requestService.findByRequestorId(userId))
                .thenReturn(requestDtoList);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(requestDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(3L), Long.class))
                .andExpect(jsonPath("$.[0].description", is("Запрос 1")))
                .andExpect(jsonPath("$.[0].requestorId", is(2)))
                .andExpect(jsonPath("$.[0].created", is(LocalDateTime.of(2026, 12, 31, 13, 45, 10).toString())))
                .andExpect(jsonPath("$.[1].id", is(4L), Long.class))
                .andExpect(jsonPath("$.[1].description", is("Запрос 2")))
                .andExpect(jsonPath("$.[1].requestorId", is(2)))
                .andExpect(jsonPath("$.[1].created", is(LocalDateTime.of(2027, 12, 31, 13, 45, 10).toString())));
    }

    @Test
    void findAll() throws Exception {

        Long userId1 = 1L;
        Long userId2 = 2L;
        Long requestId1 = 3L;
        Long requestId2 = 4L;
        Long requestId3 = 5L;

        SaveItemRequest saveItemRequest1 = makeItemRequestDto("Запрос 1");
        SaveItemRequest saveItemRequest2 = makeItemRequestDto("Запрос 2");
        SaveItemRequest saveItemRequest3 = makeItemRequestDto("Запрос 3");

        ItemRequestDto itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setId(requestId1);
        itemRequestDto1.setDescription(saveItemRequest1.getDescription());
        itemRequestDto1.setRequestorId(userId1);
        itemRequestDto1.setCreated(LocalDateTime.of(2026, 12, 31, 13, 45, 10));

        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setId(requestId2);
        itemRequestDto2.setDescription(saveItemRequest2.getDescription());
        itemRequestDto2.setRequestorId(userId1);
        itemRequestDto2.setCreated(LocalDateTime.of(2027, 12, 31, 13, 45, 10));

        ItemRequestDto itemRequestDto3 = new ItemRequestDto();
        itemRequestDto3.setId(requestId3);
        itemRequestDto3.setDescription(saveItemRequest3.getDescription());
        itemRequestDto3.setRequestorId(userId2);
        itemRequestDto3.setCreated(LocalDateTime.of(2025, 12, 31, 13, 45, 10));

        List<ItemRequestDto> requestDtoList = new ArrayList<>(List.of(itemRequestDto1, itemRequestDto2));

        when(requestService.findAll(userId2))
                .thenReturn(requestDtoList);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(requestDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(3L), Long.class))
                .andExpect(jsonPath("$.[0].description", is("Запрос 1")))
                .andExpect(jsonPath("$.[0].requestorId", is(1)))
                .andExpect(jsonPath("$.[0].created", is(LocalDateTime.of(2026, 12, 31, 13, 45, 10).toString())))
                .andExpect(jsonPath("$.[1].id", is(4L), Long.class))
                .andExpect(jsonPath("$.[1].description", is("Запрос 2")))
                .andExpect(jsonPath("$.[1].requestorId", is(1)))
                .andExpect(jsonPath("$.[1].created", is(LocalDateTime.of(2027, 12, 31, 13, 45, 10).toString())));
    }

    @Test
    void findByIdAndOwnerId() throws Exception {

        Long userId = 2L;
        Long ownerId = 5L;
        Long requestId = 4L;

        SaveItemRequest saveItemRequest = makeItemRequestDto("Запрос 1");

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(requestId);
        itemRequestDto.setDescription(saveItemRequest.getDescription());
        itemRequestDto.setRequestorId(userId);
        itemRequestDto.setCreated(LocalDateTime.of(2026, 12, 31, 13, 45, 10));

        when(requestService.findByIdAndOwnerId(requestId, ownerId))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/4")
                        .header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(saveItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestorId", is(2)))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())));
    }


    private UserDto makeUserDto(Long id, String name, String email) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);

        return dto;
    }

    private ItemDto makeItemDto(Long id, String name, String description, Boolean available, Long requestId) {
        ItemDto dto = new ItemDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setRequestId(requestId);

        return dto;
    }

    private SaveItemRequest makeItemRequestDto(String description) {
        SaveItemRequest dto = new SaveItemRequest();
        dto.setDescription(description);

        return dto;
    }
}
