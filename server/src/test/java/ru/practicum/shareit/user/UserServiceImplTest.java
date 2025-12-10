package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private final EntityManager em;
    private final UserService service;
    private final UserRepository repository;

    @Test
    void getAllUsers() {
        // given
        List<UserDto> sourceUsers = List.of(
                makeUserDto("Vasya", "vasya@email"),
                makeUserDto("Petya", "petya@email"),
                makeUserDto("Vova", "vova@email")
        );

        for (UserDto user : sourceUsers) {
            User entity = UserMapper.toUser(user);
            em.persist(entity);
        }
        em.flush();

        // when
        Collection<UserDto> targetUsers = service.getAll();

        // then
        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (UserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void saveUser() {
        // given
        UserDto userDto = makeUserDto("vasya@email.com", "Вася");

        // when
        service.create(userDto);

        // then
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void saveUserWithTheSameEmail() {
        UserDto userDto = makeUserDto("vasya123@email.com", "Вася");
        service.create(userDto);
        UserDto userDto2 = makeUserDto("vasya123@email.com", "Вася");
        assertThrows(DuplicatedDataException.class, () -> service.create(userDto2));
    }

    @Test
    void updateUser() {
        // given
        UserDto userDto = makeUserDto("vasya@email.com", "Вася");

        // when
        UserDto createdUserDto = service.create(userDto);
        createdUserDto.setEmail("1vasya@email.com");
        service.update(createdUserDto, createdUserDto.getId());

        // then
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", "1vasya@email.com")
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo("1vasya@email.com"));
    }

    @Test
    void getById() {

        // given
        UserDto userDto = makeUserDto("vasya1@email.com", "Вася1");

        // when
        UserDto createdUserDto = service.create(userDto);

        // then
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", createdUserDto.getId())
                .getSingleResult();

        assertThat(user.getId(), equalTo(createdUserDto.getId()));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void deleteUser() {
        // given
        UserDto userDto = makeUserDto("vasya@email.com", "Вася");

        // when
        UserDto createdUserDto = service.create(userDto);
        Long id = createdUserDto.getId();
        service.deleteUser(id);

        // then
        assertFalse(repository.existsById(id));
    }

    private UserDto makeUserDto(String email, String name) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);

        return dto;
    }
}