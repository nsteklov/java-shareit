package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    String error;

    public Collection<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        validate(user);
        User savedUser = userRepository.save(user);
        return UserMapper.toUserDto(savedUser);
    }

    public UserDto update(UserDto userDto, Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        User oldUser = userRepository.findById(id).get();
        User user = UserMapper.toUser(userDto);
        user.setId(id);
        if (userDto.getName() == null) {
            user.setName(oldUser.getName());
        }
        if (userDto.getEmail() == null) {
            user.setEmail(oldUser.getEmail());
        }
        validate(user);
        User updatedUser = userRepository.save(user);
        return UserMapper.toUserDto(updatedUser);
    }

    public UserDto getUser(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + id));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        userRepository.deleteById(id);
    }

    private void validate(User user) {
        Optional<User> alreadyExistUser = userRepository.findByEmailContainingIgnoreCase(user.getEmail());
        if (alreadyExistUser.isPresent() && !alreadyExistUser.get().getId().equals(user.getId())) {
            error = "Уже существует пользователь с указанным имейлом";
            throw new DuplicatedDataException(error);
        }
    }
}
