package ru.pin120.carwashAPI.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.Booking;
import ru.pin120.carwashAPI.models.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    @Query("SELECT b FROM Booking b WHERE b.bkEndTime >= :startInterval AND b.bkEndTime <= :endInterval AND b.box.boxId = :boxId ORDER BY b.bkStartTime ASC")
    List<Booking> getBoxBookings(@Param("startInterval")LocalDateTime startInterval, @Param("endInterval") LocalDateTime endInterval, @Param("boxId") Long boxId);

    @Query("SELECT b FROM Booking b WHERE :startInterval <= b.bkEndTime AND :endInterval >= b.bkStartTime AND b.box.boxId = :boxId AND b.bkStatus IN :statuses ORDER BY b.bkStartTime ASC")
    List<Booking> getCrossedBookings(@Param("startInterval")LocalDateTime startInterval, @Param("endInterval") LocalDateTime endInterval, @Param("boxId") Long boxId, @Param("statuses") List<BookingStatus> bookingStatuses);

    @Query("SELECT b FROM Booking b WHERE :startInterval <= b.bkEndTime AND :endInterval >= b.bkStartTime AND b.box.boxId = :boxId AND b.bkId <> :bkId AND b.bkStatus IN :statuses ORDER BY b.bkStartTime ASC")
    List<Booking> getCrossedBookingsWithoutCurrent(@Param("startInterval")LocalDateTime startInterval, @Param("endInterval") LocalDateTime endInterval, @Param("boxId") Long boxId, @Param("bkId") String bkId, @Param("statuses") List<BookingStatus> bookingStatuses);

    @Query("SELECT b FROM Booking b WHERE :startInterval <= b.bkEndTime AND :endInterval >= b.bkStartTime AND b.clientTransport.clTrStateNumber = :stateNumber AND b.bkStatus NOT IN :statuses ORDER BY b.bkStartTime ASC")
    List<Booking> notNegativeBookingsOfTransportInIntervalTime(@Param("startInterval")LocalDateTime startInterval, @Param("endInterval") LocalDateTime endInterval, @Param("stateNumber") String stateNumber, @Param("statuses") List<BookingStatus> bookingStatuses);

    @Query("SELECT b FROM Booking b WHERE :startInterval <= b.bkEndTime AND :endInterval >= b.bkStartTime AND b.clientTransport.clTrStateNumber = :stateNumber AND b.bkId <> :bkId AND b.bkStatus NOT IN :statuses ORDER BY b.bkStartTime ASC")
    List<Booking> notNegativeBookingsOfTransportInIntervalTime(@Param("startInterval")LocalDateTime startInterval, @Param("endInterval") LocalDateTime endInterval, @Param("stateNumber") String stateNumber, @Param("bkId") String bkId, @Param("statuses") List<BookingStatus> bookingStatuses);

    @Query("SELECT b FROM Booking b WHERE b.bkEndTime <= :startTime AND b.bkStatus IN (:statuses) AND b.box.boxId = :boxId")
    List<Booking> findNotEndedBookingsInBox(
            @Param("startTime") LocalDateTime startTime,
            @Param("statuses") List<BookingStatus> statuses,
            @Param("boxId") Long boxId
    );
}
