package ru.practicum.shareit.itemRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.SaveItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    private final EntityManager em;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    @Autowired
    private final ItemRequestService service;

    @Test
    void findByRequestorId() {

        UserDto userDto1 = makeUserDto(null, "vasya1", "vasy1a@mail.ru");
        UserDto userDto2 = makeUserDto(null, "petya1", "petya1@mail.ru");
        User user1 = UserMapper.toUser(userDto1);

        User savedUser1  = userRepository.save(user1);

        SaveItemRequest itemRequestDto1 = makeItemRequestDto("запрос 1");
        SaveItemRequest itemRequestDto2 = makeItemRequestDto("запрос 2");

        List<SaveItemRequest> sourceRequests = List.of(
                itemRequestDto1,
                itemRequestDto2);

        ItemRequest entity1 = ItemRequestMapper.toRequest(itemRequestDto1, savedUser1);
        em.persist(entity1);

        ItemRequest entity2 = ItemRequestMapper.toRequest(itemRequestDto2, savedUser1);
        em.persist(entity2);

        em.flush();

        Collection<ItemRequestDto> targetRequests = service.findByRequestorId(savedUser1.getId());

        assertThat(targetRequests, hasSize(sourceRequests.size()));
        for (SaveItemRequest saveItemRequest : sourceRequests) {
            assertThat(targetRequests, hasItem(allOf(
                    hasProperty("description", equalTo(saveItemRequest.getDescription()))
            )));
        }
    }

    @Test
    void saveItemRequest() {

        UserDto userDto1 = makeUserDto(null, "vasya1", "vasy1a@mail.ru");
        User user1 = UserMapper.toUser(userDto1);
        User savedUser1  = userRepository.save(user1);

        SaveItemRequest itemRequestDto1 = makeItemRequestDto("запрос 1");

        ItemRequestDto itemRequestDto = service.create(itemRequestDto1, savedUser1.getId());

        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir where ir.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", itemRequestDto.getId())
                .getSingleResult();

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto1.getDescription()));
    }

//    @Test
//    void findByIdAndOwnerId() {
//
//        UserDto userDto1 = makeUserDto(null, "vasya1", "vasy1a@mail.ru");
//        User user1 = UserMapper.toUser(userDto1);
//        User savedUser1  = userRepository.save(user1);
//        SaveItemRequest itemRequestDto = makeItemRequestDto("запрос 1");
//
//        ItemRequestDto createdItemRequestDto = service.create(itemRequestDto, savedUser1.getId());
//
//        String queryText = "select ir " +
//                "from ItemRequest ir " +
//                "left join Item i " +
//                "on ir.id = i.requestId " +
//                "where ir.id  = :id " +
//                " and i.owner.id  = :ownerId " +
//                "order by ir.created desc";
//
//        TypedQuery<ItemRequest> query = em.createQuery(queryText, ItemRequest.class);
//        ItemRequest itemRequest = query.setParameter("id", createdItemRequestDto.getId())
//                .setParameter("ownerId", savedUser1.getId())
//                .getSingleResult();
//
//        assertThat(itemRequest.getDescription(), equalTo(createdItemRequestDto.getDescription()));
//    }

    private SaveItemRequest makeItemRequestDto(String description) {
        SaveItemRequest dto = new SaveItemRequest();
        dto.setDescription(description);

        return dto;
    }

    private UserDto makeUserDto(Long id, String name, String email) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);

        return dto;
    }
}