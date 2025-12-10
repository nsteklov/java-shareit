package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
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
class ItemServiceImplTest {

    private final EntityManager em;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    @Autowired
    private ItemService itemService;

    @Test
    void findByOwnerId() {

        UserDto userDto1 = makeUserDto(null, "vasya1", "vasy1a@mail.ru");
        User user1 = UserMapper.toUser(userDto1);
        User savedUser1  = userRepository.save(user1);

        ItemDto itemDto1 = makeItemDto(null, "патефон1", "крутой патефон",true);
        ItemDto itemDto2 = makeItemDto(null, "граммофон1", "крутой патефон",true);

        List<ItemDto> sourceItems = List.of(
                itemDto1,
                itemDto1);

        Item entity1 = ItemMapper.toItem(itemDto1, savedUser1);
        em.persist(entity1);

        Item entity2 = ItemMapper.toItem(itemDto2, savedUser1);
        em.persist(entity2);

        em.flush();

        Collection<ItemDto> targetItems = itemService.findByOwnerId(savedUser1.getId());

        assertThat(targetItems, hasSize(sourceItems.size()));
        for (ItemDto itemDto : sourceItems) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(itemDto.getName())),
                    hasProperty("description", equalTo(itemDto.getDescription())),
                    hasProperty("available", equalTo(itemDto.getAvailable()))
            )));
        }
    }

    @Test
    void saveItem() {

        UserDto userDto1 = makeUserDto(null, "vasya2", "vasya2@mail.ru");
        User user1 = UserMapper.toUser(userDto1);
        User savedUser1 = userRepository.save(user1);

        ItemDto itemDto = makeItemDto(null, "патефон2", "крутой патефон",true);

        ItemDto createdItemDto = itemService.create(itemDto, savedUser1.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", createdItemDto.getId())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.isAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void searchAvailable() {

        UserDto userDto1 = makeUserDto(null, "vasya3", "vasy3@mail.ru");
        User user1 = UserMapper.toUser(userDto1);
        User savedUser1  = userRepository.save(user1);

        ItemDto itemDto1 = makeItemDto(null, "патефон3", "крутой патефон",true);
        ItemDto itemDto2 = makeItemDto(null, "граммофон3", "крутой патефон",true);
        ItemDto itemDto3 = makeItemDto(null, "тест", "тест",true);

        List<ItemDto> sourceItems = List.of(
                itemDto1,
                itemDto1);

        Item entity1 = ItemMapper.toItem(itemDto1, savedUser1);
        em.persist(entity1);

        Item entity2 = ItemMapper.toItem(itemDto2, savedUser1);
        em.persist(entity2);

        Item entity3 = ItemMapper.toItem(itemDto3, savedUser1);
        em.persist(entity3);

        em.flush();

        Collection<ItemDto> targetItems = itemService.searchAvailable("фон");

        assertThat(targetItems, hasSize(sourceItems.size()));
        for (ItemDto itemDto : sourceItems) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(itemDto.getName())),
                    hasProperty("description", equalTo(itemDto.getDescription())),
                    hasProperty("available", equalTo(itemDto.getAvailable()))
            )));
        }
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