package ru.pin120.carwashAPI.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.pin120.carwashAPI.models.WorkSchedule;

import java.time.LocalDate;

@Repository
public interface WorkScheduleRepository extends CrudRepository<WorkSchedule, Long> {


    @Modifying
    @Transactional
    @Query("DELETE FROM WorkSchedule ws WHERE ws.cleaner.clrId = :clrId AND ws.wsWorkDay >= :date")
    void deleteByCleanerIdAndWsWorkDayAfterOrEqual(@Param("clrId") Long clrId, @Param("date") LocalDate date);
}
