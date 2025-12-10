package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.SaveItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestDto create(SaveItemRequest saveItemRequest, Long requestorId) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь, создающий запрос на вещь, не найден"));
        ItemRequest itemRequest = ItemRequestMapper.toRequest(saveItemRequest, requestor);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toRequestDto(savedRequest, new ArrayList<>());
    }

    @Override
    public List<ItemRequestDto> findByRequestorId(Long requestorId) {
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorId(requestorId);
        List<ItemResponse> itemResponses;
        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        ItemRequestDto itemRequestDto;
        for (ItemRequest itemRequest : itemRequests) {
            itemResponses = itemRepository.findByRequestId(itemRequest.getId()).stream()
                    .map(ItemMapper::toItemResponse)
                    .collect(Collectors.toList());
            itemRequestDto = ItemRequestMapper.toRequestDto(itemRequest, itemResponses);
            itemRequestsDto.add(itemRequestDto);
        }
        return itemRequestsDto;
    }

    @Override
    public List<ItemRequestDto> findAll(Long requestorId) {
        List<ItemRequest> itemRequests = itemRequestRepository.findAll(requestorId);
        List<ItemResponse> itemResponses;
        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        ItemRequestDto itemRequestDto;
        for (ItemRequest itemRequest : itemRequests) {
            itemResponses = itemRepository.findByRequestId(itemRequest.getId()).stream()
                    .map(ItemMapper::toItemResponse)
                    .collect(Collectors.toList());
            itemRequestDto = ItemRequestMapper.toRequestDto(itemRequest,  itemResponses);
            itemRequestsDto.add(itemRequestDto);
        }
        return itemRequestsDto;
    }

    @Override
    public ItemRequestDto findByIdAndOwnerId(Long id, Long ownerId) {
        if (!itemRequestRepository.existsById(id)) {
            throw new NotFoundException("Запрос с id = " + id + " не найден");
        }
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        Optional<ItemRequest> optItemRequest = itemRequestRepository.findByIdAndOwnerId(id, ownerId);
        ItemRequest itemRequest = null;
        if (optItemRequest.isPresent()) {
            itemRequest = optItemRequest.get();
        } else {
            return null;
        }
        List<ItemResponse> itemResponses = itemRepository.findByRequestId(itemRequest.getId()).stream()
                .map(ItemMapper::toItemResponse)
                .collect(Collectors.toList());
        return ItemRequestMapper.toRequestDto(itemRequest,  itemResponses);
    }
}
