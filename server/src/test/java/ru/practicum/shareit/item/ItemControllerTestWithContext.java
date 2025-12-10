package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTestWithContext {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Test
    void saveNewItem() throws Exception {
        Long userId = 1L;
        Long itemId = 2L;

        ItemDto itemDto = makeItemDto(itemId, "патефон", "крутой патефон",true);

        when(itemService.create(any(), eq(userId)))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getById() throws Exception {
        Long userId = 1L;
        Long itemId = 2L;

        ItemDto itemDto = makeItemDto(itemId, "патефон", "крутой патефон",true);

        when(itemService.findById(itemId))
                .thenReturn(itemDto);

        mvc.perform(get("/items/2")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getByUser() throws Exception {

        Long userId = 2L;
        Long itemId1 = 3L;
        Long itemId2 = 3L;

        ItemDto itemDto1 = makeItemDto(itemId1, "патефон", "крутой патефон",true);
        ItemDto itemDto2 = makeItemDto(itemId2, "граммофон", "крутой граммофон",true);

        List<ItemDto> itemDtoList = new ArrayList<>(List.of(itemDto1, itemDto2));

        when(itemService.findByOwnerId(userId))
                .thenReturn(itemDtoList);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(itemId1), Long.class))
                .andExpect(jsonPath("$.[0].name", is("патефон")))
                .andExpect(jsonPath("$.[0].description", is("крутой патефон")))
                .andExpect(jsonPath("$.[1].available", is(true)))
                .andExpect(jsonPath("$.[1].id", is(itemId2), Long.class))
                .andExpect(jsonPath("$.[1].name",  is("граммофон")))
                .andExpect(jsonPath("$.[1].description", is("крутой граммофон")))
                .andExpect(jsonPath("$.[1].available", is(true)));
    }

    @Test
    void searchAvailable() throws Exception {

        Long userId = 2L;
        Long itemId1 = 3L;
        Long itemId2 = 3L;

        ItemDto itemDto1 = makeItemDto(itemId1, "патефон", "крутой патефон",true);
        ItemDto itemDto2 = makeItemDto(itemId2, "граммофон", "крутой граммофон",true);

        List<ItemDto> itemDtoList = new ArrayList<>(List.of(itemDto1, itemDto2));

        when(itemService.searchAvailable("фон"))
                .thenReturn(itemDtoList);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "фон")
                        .content(mapper.writeValueAsString(itemDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(itemId1), Long.class))
                .andExpect(jsonPath("$.[0].name", is("патефон")))
                .andExpect(jsonPath("$.[0].description", is("крутой патефон")))
                .andExpect(jsonPath("$.[1].available", is(true)))
                .andExpect(jsonPath("$.[1].id", is(itemId2), Long.class))
                .andExpect(jsonPath("$.[1].name",  is("граммофон")))
                .andExpect(jsonPath("$.[1].description", is("крутой граммофон")))
                .andExpect(jsonPath("$.[1].available", is(true)));
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
