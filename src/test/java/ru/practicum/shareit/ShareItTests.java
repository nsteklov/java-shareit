package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.InMemoryItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.InMemoryUserRepository;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ShareItTests {
	private final InMemoryUserRepository userRepository;
    private final InMemoryItemRepository itemRepository;

    public User newUser() {
		User user = new User();
		user.setName("vasya");
		user.setEmail("vasya@vasya.ru");
		return user;
	}

	public Item newItem() {
		Item item = new Item();
		item.setName("test");
		item.setDescription("test description");
		item.setAvailable(true);

		return item;
	}

	@Test
	public void testFindUserById() {

		User newUser = newUser();
        newUser.setEmail("test1@mail.ru");
		User savedUser = userRepository.save(newUser);
		Long id = savedUser.getId();

		Optional<User> userOptional = userRepository.findById(id);

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", id)
				);
	}

	@Test
	public void testUpdateUser() {

		User newUser = newUser();
        newUser.setEmail("test2@mail.ru");
		User savedUser = userRepository.save(newUser);
		Long id = savedUser.getId();

		User user = userRepository.findById(id).get();
		user.setName("uso");
		user.setEmail("greekVodka@uso.ru");
		userRepository.update(user);

		Optional<User> userOptional = userRepository.findById(id);

		assertThat(userOptional)
				.isPresent()
				.get()
				.usingRecursiveComparison()
				.ignoringExpectedNullFields()
				.isEqualTo(user);
	}

	@Test
	public void testDeleteUser() {

		User newUser = newUser();
        newUser.setEmail("test3@mail.ru");
		User savedUser = userRepository.save(newUser);
		Long id = savedUser.getId();

		userRepository.deleteById(id);

		assertThrows(NotFoundException.class, () -> userRepository.findById(id));
	}

    @Test
    public void testFindItemById() {

        User newUser = newUser();
        newUser.setEmail("test4@mail.ru");
        User savedUser = userRepository.save(newUser);

        Item newItem = newItem();
        Item savedItem = itemRepository.save(newItem);
        Long itemId = savedItem.getId();

        Optional<Item> itemOptional = itemRepository.findById(itemId);

        assertThat(itemOptional)
                .isPresent()
                .hasValueSatisfying(item ->
                        assertThat(item).hasFieldOrPropertyWithValue("id", itemId)
                );
    }

	@Test
	public void testFindItemByUser() {

		User newUser = newUser();
		newUser.setEmail("test5@mail.ru");
		User savedUser = userRepository.save(newUser);
		Long userId = savedUser.getId();

		Item newItem = newItem();
		newItem.setOwner(savedUser);
		Item savedItem = itemRepository.save(newItem);
		Long itemId = savedItem.getId();

		Item newItem2 = newItem();
		newItem2.setOwner(savedUser);
		Item savedItem2 = itemRepository.save(newItem2);
		Long itemId2 = savedItem2.getId();

		Collection<Item> itemsByUser = itemRepository.findByUser(userId);

		Assertions.assertThat(itemsByUser).hasSize(2);
		Assertions.assertThat(itemsByUser)
				.extracting(Item::getId)
				.containsExactlyInAnyOrder(itemId, itemId2);
	}

	@Test
	public void testSearchAvailbleItemsByText() {

		User newUser = newUser();
		newUser.setEmail("test4@mail.ru");
		User savedUser = userRepository.save(newUser);
		Long userId = savedUser.getId();

		Item newItem = newItem();
		newItem.setDescription("Тест1");
		newItem.setOwner(newUser);
		Item savedItem = itemRepository.save(newItem);
		Long itemId = savedItem.getId();

		Item newItem2 = newItem();
		newItem2.setDescription("Тест2");
		newItem2.setOwner(newUser);
		Item savedItem2 = itemRepository.save(newItem2);
		Long itemId2 = savedItem2.getId();

		Item newItem3 = newItem();
		newItem3.setName("Тест111");
		newItem3.setOwner(newUser);
		Item savedItem3 = itemRepository.save(newItem3);
		Long itemId3 = savedItem3.getId();

		Collection<Item> itemsByText = itemRepository.searchAvailable("Тест1");

		Assertions.assertThat(itemsByText).hasSize(2);
		Assertions.assertThat(itemsByText)
				.extracting(Item::getId)
				.containsExactlyInAnyOrder(itemId, itemId3);
	}

    @Test
    public void testUpdateItem() {

        User newUser = newUser();
        newUser.setEmail("test5@mail.ru");
        User savedUser = userRepository.save(newUser);
        Long userId = savedUser.getId();

        Item newItem = newItem();
        Item savedItem = itemRepository.save(newItem);
        Long itemId = savedItem.getId();

        Item item = itemRepository.findById(itemId).get();
        item.setName("Changed name");
        item.setDescription("Changed description");
        itemRepository.update(item);

        Optional<Item> itemOptional = itemRepository.findById(itemId);

        assertThat(itemOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(item);
    }
}
