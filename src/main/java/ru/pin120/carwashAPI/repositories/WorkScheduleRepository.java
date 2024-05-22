package ru.pin120.carwashAPI.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.WorkSchedule;

@Repository
public interface WorkScheduleRepository extends PagingAndSortingRepository<WorkSchedule, Long> {

}
