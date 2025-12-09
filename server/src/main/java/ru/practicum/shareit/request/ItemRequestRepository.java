package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    ItemRequest save(ItemRequest itemRequest);

    @Query("select ir " +
            "from ItemRequest ir " +
            "where ir.requestor.id = ?1 " +
            "order by ir.created desc")
    List<ItemRequest> findByRequestorId(Long requestorId);

    @Query("select ir " +
            "from ItemRequest ir " +
            "where ir.requestor.id <> ?1 " +
            "order by ir.created desc")
    List<ItemRequest> findAll(Long requestorId);

    @Query("select ir " +
            "from ItemRequest ir " +
            "left join Item i " +
            "on ir.id = i.requestId " +
            "where ir.id = ?1 " +
            " and i.owner.id = ?2 " +
            "order by ir.created desc")
    Optional<ItemRequest> findByIdAndOwnerId(Long id, Long OwnerId);
}
