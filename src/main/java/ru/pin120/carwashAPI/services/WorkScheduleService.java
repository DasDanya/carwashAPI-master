package ru.pin120.carwashAPI.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pin120.carwashAPI.dtos.CleanerDTO;
import ru.pin120.carwashAPI.dtos.WorkScheduleDTO;
import ru.pin120.carwashAPI.models.Cleaner;
import ru.pin120.carwashAPI.models.WorkSchedule;
import ru.pin120.carwashAPI.repositories.CleanerRepository;
import ru.pin120.carwashAPI.repositories.WorkScheduleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final CleanerRepository cleanerRepository;

    public WorkScheduleService(WorkScheduleRepository workScheduleRepository, CleanerRepository cleanerRepository) {
        this.workScheduleRepository = workScheduleRepository;
        this.cleanerRepository = cleanerRepository;
    }

    public void deleteByClrIdAndStartDate(Long clrId, LocalDate start){
        workScheduleRepository.deleteByCleanerIdAndWsWorkDayAfterOrEqual(clrId, start);
    }

    public List<WorkSchedule> getAll() {
//        Sort sort = Sort.by("cleaner.clrSurname", "cleaner.clrName", "cleaner.clrPatronymic", "wsStartDay", "wsStartTime", "wsEndDay");
//        return (List<WorkSchedule>) workScheduleRepository.findAll(sort);
        return (List<WorkSchedule>) workScheduleRepository.findAll();
    }

    public List<WorkSchedule> create(List<CleanerDTO> cleanerDTOS) {
        List<WorkSchedule> createdWorkScheduleItems = new ArrayList<>();
        for(CleanerDTO cleanerDTO: cleanerDTOS){
            Optional<Cleaner> cleanerOptional = cleanerRepository.findById(cleanerDTO.getClrId());
            if(cleanerOptional.isEmpty()){
                throw new EntityNotFoundException(String.format("Мойщика с id=%d не существует в базе данных", cleanerDTO.getClrId()));
            }else{
                Cleaner cleaner = cleanerOptional.get();
                for(WorkSchedule workSchedule: cleanerDTO.getWorkSchedules()){
                    WorkSchedule createdWorkScheduleItem = new WorkSchedule();
                    createdWorkScheduleItem.setWsWorkDay(workSchedule.getWsWorkDay());
                    createdWorkScheduleItem.setCleaner(cleaner);

                    createdWorkScheduleItems.add(createdWorkScheduleItem);
                }
            }
        }
        workScheduleRepository.saveAll(createdWorkScheduleItems);
        return createdWorkScheduleItems;
    }

    public void delete(List<WorkSchedule> workSchedules) {
        workScheduleRepository.deleteAll(workSchedules);
    }
}
