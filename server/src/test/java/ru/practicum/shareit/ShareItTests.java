//package ru.practicum.shareit;
//
//import lombok.RequiredArgsConstructor;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import ru.practicum.shareit.booking.Booking;
//import ru.practicum.shareit.booking.BookingRepository;
//import ru.practicum.shareit.booking.Status;
//import ru.practicum.shareit.booking.dto.BookingDto;
//import ru.practicum.shareit.item.ItemRepository;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.user.UserRepository;
//import ru.practicum.shareit.user.User;
//import ru.practicum.shareit.booking.BookingServiceImpl;
//
//import java.time.LocalDateTime;
//import java.util.Collection;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest
//@AutoConfigureTestDatabase
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//class ShareItTests {
//	private final UserRepository userRepository;
//    private final ItemRepository itemRepository;
//    private final BookingRepository bookingRepository;
//    private final BookingServiceImpl bookingService;
//
//    public User newUser() {
//		User user = new User();
//		user.setName("vasya");
//		user.setEmail("vasya@vasya.ru");
//		return user;
//	}
//
//	public Item newItem() {
//		Item item = new Item();
//		item.setName("test");
//		item.setDescription("test description");
//		item.setAvailable(true);
//
//		return item;
//	}
//
//	@Test
//	public void testFindUserById() {
//
//		User newUser = newUser();
//        newUser.setEmail("test1@mail.ru");
//		User savedUser = userRepository.save(newUser);
//		Long id = savedUser.getId();
//
//		Optional<User> userOptional = userRepository.findById(id);
//
//		assertThat(userOptional)
//				.isPresent()
//				.hasValueSatisfying(user ->
//						assertThat(user).hasFieldOrPropertyWithValue("id", id)
//				);
//	}
//
//	@Test
//	public void testUpdateUser() {
//
//		User newUser = newUser();
//        newUser.setEmail("test2@mail.ru");
//		User savedUser = userRepository.save(newUser);
//		Long id = savedUser.getId();
//
//		User user = userRepository.findById(id).get();
//		user.setName("uso");
//		user.setEmail("greekVodka@uso.ru");
//		userRepository.save(user);
//
//		Optional<User> userOptional = userRepository.findById(id);
//
//		assertThat(userOptional)
//				.isPresent()
//				.get()
//				.usingRecursiveComparison()
//				.ignoringExpectedNullFields()
//				.isEqualTo(user);
//	}
//
//	@Test
//	public void testDeleteUser() {
//
//		User newUser = newUser();
//        newUser.setEmail("test3@mail.ru");
//		User savedUser = userRepository.save(newUser);
//		Long id = savedUser.getId();
//
//		userRepository.deleteById(id);
//        assertThat(userRepository.findById(id).isEmpty());
//	}
//
//    @Test
//    public void testFindItemById() {
//
//        User newUser = newUser();
//        newUser.setEmail("test4@mail.ru");
//        User savedUser = userRepository.save(newUser);
//
//        Item newItem = newItem();
//        Item savedItem = itemRepository.save(newItem);
//        Long itemId = savedItem.getId();
//
//        Optional<Item> itemOptional = itemRepository.findById(itemId);
//
//        assertThat(itemOptional)
//                .isPresent()
//                .hasValueSatisfying(item ->
//                        assertThat(item).hasFieldOrPropertyWithValue("id", itemId)
//                );
//    }
//
//	@Test
//	public void testFindItemByUser() {
//
//		User newUser = newUser();
//		newUser.setEmail("test6@mail.ru");
//		User savedUser = userRepository.save(newUser);
//		Long userId = savedUser.getId();
//
//		Item newItem = newItem();
//		newItem.setOwner(savedUser);
//		Item savedItem = itemRepository.save(newItem);
//		Long itemId = savedItem.getId();
//
//		Item newItem2 = newItem();
//		newItem2.setOwner(savedUser);
//		Item savedItem2 = itemRepository.save(newItem2);
//		Long itemId2 = savedItem2.getId();
//
//		Collection<Item> itemsByUser = itemRepository.findByOwnerId(userId);
//
//		Assertions.assertThat(itemsByUser).hasSize(2);
//		Assertions.assertThat(itemsByUser)
//				.extracting(Item::getId)
//				.containsExactlyInAnyOrder(itemId, itemId2);
//	}
//
//	@Test
//	public void testSearchAvailableItemsByText() {
//
//		User newUser = newUser();
//		newUser.setEmail("test7@mail.ru");
//		User savedUser = userRepository.save(newUser);
//		Long userId = savedUser.getId();
//
//		Item newItem = newItem();
//		newItem.setDescription("Тест1");
//		newItem.setOwner(savedUser);
//		Item savedItem = itemRepository.save(newItem);
//		Long itemId = savedItem.getId();
//
//		Item newItem2 = newItem();
//		newItem2.setDescription("Тест2");
//		newItem2.setOwner(savedUser);
//		Item savedItem2 = itemRepository.save(newItem2);
//		Long itemId2 = savedItem2.getId();
//
//		Item newItem3 = newItem();
//		newItem3.setName("Тест111");
//		newItem3.setOwner(savedUser);
//		Item savedItem3 = itemRepository.save(newItem3);
//		Long itemId3 = savedItem3.getId();
//
//		Collection<Item> itemsByText = itemRepository.findAvailable("Тест1");
//
//		Assertions.assertThat(itemsByText).hasSize(2);
//		Assertions.assertThat(itemsByText)
//				.extracting(Item::getId)
//				.containsExactlyInAnyOrder(itemId, itemId3);
//	}
//
//    @Test
//    public void testUpdateItem() {
//
//        User newUser = newUser();
//        newUser.setEmail("test5@mail.ru");
//        User savedUser = userRepository.save(newUser);
//        Long userId = savedUser.getId();
//
//        Item newItem = newItem();
//        Item savedItem = itemRepository.save(newItem);
//        Long itemId = savedItem.getId();
//
//        Item item = itemRepository.findById(itemId).get();
//        item.setName("Changed name");
//        item.setDescription("Changed description");
//        itemRepository.save(item);
//
//        Optional<Item> itemOptional = itemRepository.findById(itemId);
//
//        assertThat(itemOptional)
//                .isPresent()
//                .get()
//                .usingRecursiveComparison()
//                .ignoringExpectedNullFields()
//                .isEqualTo(item);
//    }
//
//    @Test
//    public void testApproveBooking() {
//        User newUser = new User();
//        newUser.setName("vasya2");
//        newUser.setEmail("vasya2@vasya.ru");
//        User savedUser = userRepository.save(newUser);
//
//        User newUser2 = new User();
//        newUser2.setName("vasya3");
//        newUser2.setEmail("vasya3@vasya.ru");
//        User savedUser2 = userRepository.save(newUser2);
//
//        Item newItem = new Item();
//        newItem.setName("test2");
//        newItem.setDescription("test2 description");
//        newItem.setAvailable(true);
//        newItem.setOwner(savedUser2);
//        Item savedItem = itemRepository.save(newItem);
//        Booking newBooking = new Booking();
//        newBooking.setStart(LocalDateTime.of(2026, 01, 01, 00, 00, 00));
//        newBooking.setStart(LocalDateTime.of(2027, 01, 01, 00, 00, 00));
//        newBooking.setItem(savedItem);
//        newBooking.setBooker(savedUser);
//        Booking savedBooking = bookingRepository.save(newBooking);
//        BookingDto bookingDto = bookingService.approve(savedBooking.getId(), true, newItem.getOwner().getId());
//
//        assertEquals(bookingDto.getStatus(), Status.APPROVED);
//    }
//
//    @Test
//    public void findBookingById() {
//        User newUser = new User();
//        newUser.setName("vasya4");
//        newUser.setEmail("vasya4@vasya.ru");
//        User savedUser = userRepository.save(newUser);
//
//        User newUser2 = new User();
//        newUser2.setName("vasya5");
//        newUser2.setEmail("vasya5@vasya.ru");
//        User savedUser2 = userRepository.save(newUser2);
//
//        Item newItem = new Item();
//        newItem.setName("test3");
//        newItem.setDescription("test3 description");
//        newItem.setAvailable(true);
//        newItem.setOwner(savedUser2);
//        Item savedItem = itemRepository.save(newItem);
//        Booking newBooking = new Booking();
//        newBooking.setStart(LocalDateTime.of(2027, 01, 01, 00, 00, 00));
//        newBooking.setStart(LocalDateTime.of(2028, 01, 01, 00, 00, 00));
//        newBooking.setItem(savedItem);
//        newBooking.setBooker(savedUser);
//        Booking savedBooking = bookingRepository.save(newBooking);
//        Long bookingId = savedBooking.getId();
//
//        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
//
//        assertThat(bookingOptional)
//                .isPresent()
//                .hasValueSatisfying(booking ->
//                        assertThat(booking).hasFieldOrPropertyWithValue("id", bookingId)
//                );
//    }
//}
