package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking save(Booking booking);

    Optional<Booking> findById(Long id);

    List<Booking> findByItemId(Long itemId);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "order by b.start desc")
    List<Booking> findByBookerIdAll(Long bookerId);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "and b.status = 'APPROVED' " +
            "order by b.start desc")
    List<Booking> findByBookerIdCurrent(Long bookerId, LocalDateTime now);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.end < ?2 " +
            "and b.status = 'APPROVED' " +
            "order by b.start desc")
    List<Booking> findByBookerIdPast(Long bookerId, LocalDateTime now);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start > ?2 " +
            "and b.status = 'APPROVED' " +
            "order by b.start desc")
    List<Booking> findByBookerIdFuture(Long bookerId, LocalDateTime now);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.status = 'WAITING' " +
            "order by b.start desc")
    List<Booking> findByBookerIdWaiting(Long bookerId);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and (b.status = 'REJECTED' " +
            "    or b.status = 'CANCELED') " +
            "order by b.start desc")
    List<Booking> findByBookerIdRejected(Long bookerId);

    @Query("select b " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "order by b.start desc")
    List<Booking> findByOwnerIdAll(Long ownerId);

    @Query("select b " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "and b.status = 'APPROVED' " +
            "order by b.start desc")
    List<Booking> findByOwnerIdCurrent(Long ownerId, LocalDateTime now);

    @Query("select b " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.end < ?2 " +
            "and b.status = 'APPROVED' " +
            "order by b.start desc")
    List<Booking> findByOwnerIdPast(Long ownerId, LocalDateTime now);

    @Query("select b " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start > ?2 " +
            "and b.status = 'APPROVED' " +
            "order by b.start desc")
    List<Booking> findByOwnerIdFuture(Long ownerId, LocalDateTime now);

    @Query("select b " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.status = 'WAITING' " +
            "order by b.start desc")
    List<Booking> findByOwnerIdWaiting(Long ownerId);

    @Query("select b " +
            "from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and (b.status = 'REJECTED' " +
            "    or b.status = 'CANCELED') " +
            "order by b.start desc")
    List<Booking> findByOwnerIdRejected(Long ownerId);
}
