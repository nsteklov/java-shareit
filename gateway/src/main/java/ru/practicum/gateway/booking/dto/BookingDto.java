package ru.practicum.gateway.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.gateway.user.dto.UserDto;
import ru.practicum.gateway.booking.Status;
import ru.practicum.gateway.item.dto.ItemDtoWithoutComments;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDtoWithoutComments item;
    private UserDto booker;
    private Status status;
}
