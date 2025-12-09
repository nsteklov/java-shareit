package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.SaveItemRequest;

import java.util.List;

public interface ItemRequestService {
    public ItemRequestDto create(SaveItemRequest saveItemRequest, Long requestorId);

    public List<ItemRequestDto> findByRequestorId(Long requestorId);

    public List<ItemRequestDto> findAll(Long requestorId);

    public ItemRequestDto findByIdAndOwnerId(Long id, Long ownerId);
}
