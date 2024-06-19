package ru.pin120.carwashAPI.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.Booking;
import ru.pin120.carwashAPI.models.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий заказа
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    /**
     * Получение списка заказов для указанного бокса в заданном интервале времени
     *
     * @param startInterval начальная дата и время интервала
     * @param endInterval конечная дата и время интервала
     * @param boxId id бокса
     * @return список заказов для указанного бокса в заданном интервале времени
     */
    @Query("SELECT b FROM Booking b WHERE b.bkEndTime >= :startInterval AND b.bkEndTime <= :endInterval AND b.box.boxId = :boxId ORDER BY b.bkStartTime ASC")
    List<Booking> getBoxBookings(@Param("startInterval")LocalDateTime startInterval, @Param("endInterval") LocalDateTime endInterval, @Param("boxId") Long boxId);

    /**
     * Получение списка пересекающихся заказов для указанного бокса и интервала времени с заданными статусами
     *
     * @param startInterval начальная дата и время интервала
     * @param endInterval конечная дата и время интервала
     * @param boxId id бокса
     * @param bookingStatuses список статусов заказа
     * @return список пересекающихся заказов для указанного бокса и интервала времени
     */
    @Query("SELECT b FROM Booking b WHERE :startInterval <= b.bkEndTime AND :endInterval >= b.bkStartTime AND b.box.boxId = :boxId AND b.bkStatus IN :statuses ORDER BY b.bkStartTime ASC")
    List<Booking> getCrossedBookings(@Param("startInterval")LocalDateTime startInterval, @Param("endInterval") LocalDateTime endInterval, @Param("boxId") Long boxId, @Param("statuses") List<BookingStatus> bookingStatuses);

    /**
     * Получение списка пересекающихся заказов для указанного бокса и интервала времени с заданными статусами, исключая текущий заказ
     *
     * @param startInterval начальная дата и время интервала
     * @param endInterval конечная дата и время интервала
     * @param boxId id бокса
     * @param bkId id текущего заказа
     * @param bookingStatuses список статусов заказов
     * @return список пересекающихся заказов для указанного бокса и интервала времени, исключая текущее заказ
     */
    @Query("SELECT b FROM Booking b WHERE :startInterval <= b.bkEndTime AND :endInterval >= b.bkStartTime AND b.box.boxId = :boxId AND b.bkId <> :bkId AND b.bkStatus IN :statuses ORDER BY b.bkStartTime ASC")
    List<Booking> getCrossedBookingsWithoutCurrent(@Param("startInterval")LocalDateTime startInterval, @Param("endInterval") LocalDateTime endInterval, @Param("boxId") Long boxId, @Param("bkId") String bkId, @Param("statuses") List<BookingStatus> bookingStatuses);

    /**
     * Получение списка заказов для транспорта с указанным государственным номером в заданном интервале времени и исключением указанных статусов
     *
     * @param startInterval начальная дата и время интервала
     * @param endInterval конечная дата и время интервала
     * @param stateNumber государственный номер транспортного средства
     * @param bookingStatuses список статусов заказов для исключения
     * @return список заказов для транспорта с указанным государственным номером
     */
    @Query("SELECT b FROM Booking b WHERE :startInterval <= b.bkEndTime AND :endInterval >= b.bkStartTime AND b.clientTransport.clTrStateNumber = :stateNumber AND b.bkStatus NOT IN :statuses ORDER BY b.bkStartTime ASC")
    List<Booking> notNegativeBookingsOfTransportInIntervalTime(@Param("startInterval")LocalDateTime startInterval, @Param("endInterval") LocalDateTime endInterval, @Param("stateNumber") String stateNumber, @Param("statuses") List<BookingStatus> bookingStatuses);

    /**
     * Получение списка заказов для транспорта с указанным государственным номером в заданном интервале времени, исключая текущий заказ и указанные статусы
     *
     * @param startInterval начальная дата и время интервала
     * @param endInterval конечная дата и время интервала
     * @param stateNumber государственный номер транспортного средства
     * @param bkId id текущего заказа для исключения
     * @param bookingStatuses список статусов заказа для исключения
     * @return список заказов для транспорта с указанным государственным номером, исключая текущий заказ
     */
    @Query("SELECT b FROM Booking b WHERE :startInterval <= b.bkEndTime AND :endInterval >= b.bkStartTime AND b.clientTransport.clTrStateNumber = :stateNumber AND b.bkId <> :bkId AND b.bkStatus NOT IN :statuses ORDER BY b.bkStartTime ASC")
    List<Booking> notNegativeBookingsOfTransportInIntervalTime(@Param("startInterval")LocalDateTime startInterval, @Param("endInterval") LocalDateTime endInterval, @Param("stateNumber") String stateNumber, @Param("bkId") String bkId, @Param("statuses") List<BookingStatus> bookingStatuses);

    /**
     * Получение списка незавершенных заказов для указанного бокса с заданными статусами и до указанного времени начала заказа
     *
     * @param startTime время, до которого ищутся заказы
     * @param statuses список статусов заказов
     * @param boxId id бокса
     * @return список незавершенных заказов для указанного бокса
     */
    @Query("SELECT b FROM Booking b WHERE b.bkEndTime <= :startTime AND b.bkStatus IN (:statuses) AND b.box.boxId = :boxId")
    List<Booking> findNotEndedBookingsInBox(
            @Param("startTime") LocalDateTime startTime,
            @Param("statuses") List<BookingStatus> statuses,
            @Param("boxId") Long boxId
    );
}
