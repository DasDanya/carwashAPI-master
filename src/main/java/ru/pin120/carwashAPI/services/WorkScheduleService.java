package ru.pin120.carwashAPI.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.pin120.carwashAPI.models.WorkSchedule;
import ru.pin120.carwashAPI.repositories.WorkScheduleRepository;

import java.util.List;

@Service
public class WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;

    public WorkScheduleService(WorkScheduleRepository workScheduleRepository) {
        this.workScheduleRepository = workScheduleRepository;
    }

    public List<WorkSchedule> getAll() {
        Sort sort = Sort.by("cleaner.clrSurname", "cleaner.clrName", "cleaner.clrPatronymic", "wsStartDay", "wsStartTime", "wsEndDay");
        return (List<WorkSchedule>) workScheduleRepository.findAll(sort);
    }
}
