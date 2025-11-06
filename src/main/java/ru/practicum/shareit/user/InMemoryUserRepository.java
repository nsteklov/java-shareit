package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    String error;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findById(Long idOfUser) {
        User user = users.keySet()
                .stream()
                .map(id -> Optional.ofNullable(users.get(idOfUser)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + idOfUser + " не найден"));
        return Optional.ofNullable(user);
    }

    @Override
    public User save(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        if (newUser.getId() == null) {
            error = "Id должен быть указан";
            throw new NotFoundException(error);
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            oldUser.setName(newUser.getName());
            oldUser.setEmail(newUser.getEmail());
            users.put(oldUser.getId(), oldUser);
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @Override
    public boolean existsById(Long id) {
         return users.keySet()
                .stream()
                .anyMatch(id::equals);
    }

    @Override
    public void deleteById(Long id) {
        if (!existsById(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        users.remove(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<User> optUser = users.values()
                .stream()
                .filter(currentUser -> currentUser.getEmail().equals(email))
                .findFirst();
        return optUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
