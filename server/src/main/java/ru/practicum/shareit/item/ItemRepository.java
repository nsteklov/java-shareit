package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Item save(Item item);

    Optional<Item> findById(Long id);

    List<Item> findByOwnerId(Long userId);

    List<Item> findByRequestId(Long requestId);

    @Query("select it " +
            "from Item it " +
            "where it.available " +
            "and (upper(trim(it.name)) like upper(concat('%', ?1, '%')) " +
            "    or upper(trim(it.description)) like upper(concat('%', ?1, '%')))")
    List<Item> findAvailable(String text);

    boolean existsById(Long id);
}
