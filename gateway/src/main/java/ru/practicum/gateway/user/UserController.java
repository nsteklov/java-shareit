package ru.practicum.gateway.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.exception.ValidationException;
import ru.practicum.gateway.user.dto.UserDto;

import java.util.Optional;

@RequestMapping(path = "/users")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserClient client;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return client.getAll();
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new ValidationException("Имя пользователя не может быть пустым");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        return client.create(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody UserDto userDto, @PathVariable Long id) {
        if (userDto.getName() != null && userDto.getName().isBlank()) {
            throw new ValidationException("Имя пользователя не может быть пустым");
        }
        if (userDto.getEmail() != null && (userDto.getEmail().isBlank() || !userDto.getEmail().contains("@"))) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        return client.update(userDto, id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        return client.getUser(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        client.deleteUser(id);
    }
}
