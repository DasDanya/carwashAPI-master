package ru.pin120.carwashAPI.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.pin120.carwashAPI.models.WorkSchedule;


import java.time.LocalDate;
import java.util.List;

/**
 * Репозиторий рабочих дней
 */
@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {

    /**
     * Получение списка рабочих дней мойщика в определенный период
     * @param startInterval начало интервала поиска
     * @param endInterval конец интервала поиска
     * @param clrId id мойщика
     * @return список рабочих дней мойщика
     */
    @Query("SELECT w FROM WorkSchedule w WHERE w.wsWorkDay >= :startInterval AND w.wsWorkDay <= :endInterval " +
            " AND w.cleaner.clrId = :clrId")
    List<WorkSchedule> get(@Param("startInterval") LocalDate startInterval,
                           @Param("endInterval") LocalDate endInterval,
                           @Param("clrId") Long clrId);



    /**
     * Получение рабочих дней мойщика в указанном интервале времени с пагинацией
     *
     * @param startInterval начальная дата интервала для поиска (включительно)
     * @param endInterval конечная дата интервала для поиска (включительно)
     * @param clrId id мойщика для поиска расписания работ
     * @param pageable объект, предоставляющий информацию о странице результатов и сортировке
     * @return список рабочих дней мойщика, удовлетворяющий критериям поиска
     */
    @Query("SELECT w FROM WorkSchedule w WHERE w.wsWorkDay >= :startInterval AND w.wsWorkDay <= :endInterval AND w.cleaner.clrId = :clrId")
    List<WorkSchedule> get(@Param("startInterval") LocalDate startInterval, @Param("endInterval") LocalDate endInterval, @Param("clrId") Long clrId, Pageable pageable);


    /**
     * Удаление рабочих дней указанного мойщика, начиная с указанной даты.
     *
     * @param clrId id мойщика
     * @param date начальная дата для удаления
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM WorkSchedule ws WHERE ws.cleaner.clrId = :clrId AND ws.wsWorkDay >= :date")
    void deleteByCleanerIdAndWsWorkDayAfterOrEqual(@Param("clrId") Long clrId, @Param("date") LocalDate date);

}
