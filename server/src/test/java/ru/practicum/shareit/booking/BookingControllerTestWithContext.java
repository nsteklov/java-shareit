package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SaveBookingRequest;
import ru.practicum.shareit.item.dto.ItemDtoWithoutComments;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTestWithContext {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;


    @Test
    void saveNewBooking() throws Exception {
        Long bookerId = 2L;
        Long itemId = 3L;

        UserDto userDto = makeUserDto(bookerId, "vasya", "vasya@mail.ru");
        ItemDtoWithoutComments itemDto = makeItemDto(itemId, "патефон", "крутой патефон",true);
        SaveBookingRequest saveBookingRequest = makeSaveBookingRequest(LocalDateTime.of(2026, 12, 31, 13, 45, 10), LocalDateTime.of(2028, 12, 31, 13, 45, 10), itemId);

        BookingDto bookingDto = new BookingDto(1L,
                saveBookingRequest.getStart(),
                saveBookingRequest.getEnd(),
                itemDto,
                userDto,
                Status.WAITING);

        when(bookingService.create(saveBookingRequest, bookerId))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .content(mapper.writeValueAsString(saveBookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void approve() throws Exception {
        Long bookerId = 2L;
        Long itemId = 3L;

        UserDto userDto = makeUserDto(bookerId, "vasya", "vasya@mail.ru");
        ItemDtoWithoutComments itemDto = makeItemDto(itemId, "патефон", "крутой патефон",true);
        SaveBookingRequest saveBookingRequest = makeSaveBookingRequest(LocalDateTime.of(2026, 12, 31, 13, 45, 10), LocalDateTime.of(2028, 12, 31, 13, 45, 10), itemId);

        BookingDto bookingDtoApproved = new BookingDto(1L,
                saveBookingRequest.getStart(),
                saveBookingRequest.getEnd(),
                itemDto,
                userDto,
                Status.APPROVED);

        when(bookingService.approve(1L, true, 4L))
                .thenReturn(bookingDtoApproved);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 4L)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDtoApproved))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoApproved.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDtoApproved.getEnd().toString())))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void getById() throws Exception {

        Long bookerId = 2L;
        Long itemId = 3L;

        UserDto userDto = makeUserDto(bookerId, "vasya", "vasya@mail.ru");
        ItemDtoWithoutComments itemDto = makeItemDto(itemId, "патефон", "крутой патефон",true);
        SaveBookingRequest saveBookingRequest = makeSaveBookingRequest(LocalDateTime.of(2026, 12, 31, 13, 45, 10), LocalDateTime.of(2028, 12, 31, 13, 45, 10), itemId);

        BookingDto bookingDto = new BookingDto(1L,
                saveBookingRequest.getStart(),
                saveBookingRequest.getEnd(),
                itemDto,
                userDto,
                Status.WAITING);

        when(bookingService.findById(1L, bookerId))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", bookerId)
                        .content(mapper.writeValueAsString(saveBookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getByUserId() throws Exception {

        Long bookerId = 2L;
        Long itemId = 3L;

        UserDto userDto1 = makeUserDto(bookerId, "vasya", "vasya@mail.ru");
        ItemDtoWithoutComments itemDto1 = makeItemDto(itemId, "патефон", "крутой патефон",true);
        SaveBookingRequest saveBookingRequest1 = makeSaveBookingRequest(LocalDateTime.of(2026, 12, 31, 13, 45, 10), LocalDateTime.of(2028, 12, 31, 13, 45, 10), itemId);

        BookingDto bookingDto1 = new BookingDto(1L,
                saveBookingRequest1.getStart(),
                saveBookingRequest1.getEnd(),
                itemDto1,
                userDto1,
                Status.APPROVED);

        ItemDtoWithoutComments itemDto2 = makeItemDto(itemId, "граммофон", "крутой патефон",true);
        SaveBookingRequest saveBookingRequest2 = makeSaveBookingRequest(LocalDateTime.of(2027, 12, 31, 13, 45, 10), LocalDateTime.of(2029, 12, 31, 13, 45, 10), itemId);

        BookingDto bookingDto2 = new BookingDto(2L,
                saveBookingRequest2.getStart(),
                saveBookingRequest2.getEnd(),
                itemDto2,
                userDto1,
                Status.APPROVED);

        List<BookingDto> bookingDtoList = new ArrayList<>(List.of(bookingDto1, bookingDto2));

        when(bookingService.findByUserId(2L, "ALL"))
                .thenReturn(bookingDtoList);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .content(mapper.writeValueAsString(bookingDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$.[0].start", is(LocalDateTime.of(2026, 12, 31, 13, 45, 10).toString())))
                .andExpect(jsonPath("$.[0].end", is(LocalDateTime.of(2028, 12, 31, 13, 45, 10).toString())))
                .andExpect(jsonPath("$.[1].status", is("APPROVED")))
                .andExpect(jsonPath("$.[1].id", is(2L), Long.class))
                .andExpect(jsonPath("$.[1].start", is(LocalDateTime.of(2027, 12, 31, 13, 45, 10).toString())))
                .andExpect(jsonPath("$.[1].end", is(LocalDateTime.of(2029, 12, 31, 13, 45, 10).toString())))
                .andExpect(jsonPath("$.[1].status", is("APPROVED")));
    }

    @Test
    void getByOwnerId() throws Exception {

        Long bookerId = 2L;
        Long ownerId = 2L;
        Long itemId = 3L;

        UserDto userDto1 = makeUserDto(bookerId, "vasya", "vasya@mail.ru");
        ItemDtoWithoutComments itemDto1 = makeItemDto(itemId, "патефон", "крутой патефон",true);
        SaveBookingRequest saveBookingRequest1 = makeSaveBookingRequest(LocalDateTime.of(2026, 12, 31, 13, 45, 10), LocalDateTime.of(2028, 12, 31, 13, 45, 10), itemId);

        BookingDto bookingDto1 = new BookingDto(1L,
                saveBookingRequest1.getStart(),
                saveBookingRequest1.getEnd(),
                itemDto1,
                userDto1,
                Status.APPROVED);

        UserDto userDto2 = makeUserDto(ownerId, "petya", "petya@mail.ru");
        ItemDtoWithoutComments itemDto2 = makeItemDto(itemId, "граммофон", "крутой патефон",true);
        SaveBookingRequest saveBookingRequest2 = makeSaveBookingRequest(LocalDateTime.of(2027, 12, 31, 13, 45, 10), LocalDateTime.of(2029, 12, 31, 13, 45, 10), itemId);

        BookingDto bookingDto2 = new BookingDto(2L,
                saveBookingRequest2.getStart(),
                saveBookingRequest2.getEnd(),
                itemDto2,
                userDto1,
                Status.APPROVED);

        List<BookingDto> bookingDtoList = new ArrayList<>(List.of(bookingDto1, bookingDto2));

        when(bookingService.findByOwnerId(ownerId, "ALL"))
                .thenReturn(bookingDtoList);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(bookingDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$.[0].start", is(LocalDateTime.of(2026, 12, 31, 13, 45, 10).toString())))
                .andExpect(jsonPath("$.[0].end", is(LocalDateTime.of(2028, 12, 31, 13, 45, 10).toString())))
                .andExpect(jsonPath("$.[1].status", is("APPROVED")))
                .andExpect(jsonPath("$.[1].id", is(2L), Long.class))
                .andExpect(jsonPath("$.[1].start", is(LocalDateTime.of(2027, 12, 31, 13, 45, 10).toString())))
                .andExpect(jsonPath("$.[1].end", is(LocalDateTime.of(2029, 12, 31, 13, 45, 10).toString())))
                .andExpect(jsonPath("$.[1].status", is("APPROVED")));
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

    private ItemDtoWithoutComments makeItemDto(Long id, String name, String description, Boolean available) {
        ItemDtoWithoutComments dto = new ItemDtoWithoutComments();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);

        return dto;
    }
}
