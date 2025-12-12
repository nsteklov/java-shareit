package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();

    Optional<User> findById(Long id);

    User save(User user);

    boolean existsById(Long id);

    void deleteById(Long id);

    Optional<User> findByEmailContainingIgnoreCase(String email);
}