package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> findAll();

    Optional<User> findById(Long id);

    User save(User user);

    User update(User user);

    boolean existsById(Long id);

    void deleteById(Long id);

    Optional<User> findByEmail(String email);
}