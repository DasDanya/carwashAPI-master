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

@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {


    @Query("SELECT w FROM WorkSchedule w WHERE w.wsWorkDay >= :startInterval AND w.wsWorkDay <= :endInterval AND w.cleaner.clrId = :clrId")
    List<WorkSchedule> get(@Param("startInterval") LocalDate startInterval, @Param("endInterval") LocalDate endInterval, @Param("clrId") Long clrId, Pageable pageable);

    @Query("SELECT w FROM WorkSchedule w WHERE w.wsWorkDay >= :startInterval AND w.wsWorkDay <= :endInterval AND w.cleaner.clrId = :clrId")
    List<WorkSchedule> get(@Param("startInterval") LocalDate startInterval, @Param("endInterval") LocalDate endInterval, @Param("clrId") Long clrId);

    @Modifying
    @Transactional
    @Query("DELETE FROM WorkSchedule ws WHERE ws.cleaner.clrId = :clrId AND ws.wsWorkDay >= :date")
    void deleteByCleanerIdAndWsWorkDayAfterOrEqual(@Param("clrId") Long clrId, @Param("date") LocalDate date);

}
