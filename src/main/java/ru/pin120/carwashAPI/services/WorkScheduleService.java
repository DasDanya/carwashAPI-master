package ru.pin120.carwashAPI.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.pin120.carwashAPI.dtos.CleanerDTO;
import ru.pin120.carwashAPI.dtos.ResultCreateWorkSchedulesDTO;
import ru.pin120.carwashAPI.dtos.WorkScheduleDTO;
import ru.pin120.carwashAPI.models.Box;
import ru.pin120.carwashAPI.models.Cleaner;
import ru.pin120.carwashAPI.models.WorkSchedule;
import ru.pin120.carwashAPI.repositories.CleanerRepository;
import ru.pin120.carwashAPI.repositories.WorkScheduleRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final CleanerRepository cleanerRepository;

    private final int COUNT_ITEMS_IN_PAGE = 30;

    public WorkScheduleService(WorkScheduleRepository workScheduleRepository, CleanerRepository cleanerRepository) {
        this.workScheduleRepository = workScheduleRepository;
        this.cleanerRepository = cleanerRepository;
    }

    public void deleteByClrIdAndStartDate(Long clrId, LocalDate start){
        workScheduleRepository.deleteByCleanerIdAndWsWorkDayAfterOrEqual(clrId, start);
    }

    public List<WorkSchedule> get(LocalDate startInterval, LocalDate endInterval, Long clrId, Integer pageIndex){
        if(pageIndex != null) {
            Pageable pageable = PageRequest.of(pageIndex, COUNT_ITEMS_IN_PAGE, Sort.by("wsWorkDay"));
            return workScheduleRepository.get(startInterval, endInterval, clrId, pageable);
        }else{
            return workScheduleRepository.get(startInterval,endInterval,clrId);
        }
    }

    public static WorkScheduleDTO toDTO(WorkSchedule workSchedule){
        return new WorkScheduleDTO(
                workSchedule.getWsId(),
                workSchedule.getWsWorkDay()
        );
    }

    public ResultCreateWorkSchedulesDTO create(List<CleanerDTO> cleanerDTOS) {
        List<WorkSchedule> createdWorkScheduleItems = new ArrayList<>();
        String conflictMessage = "";
        for(CleanerDTO cleanerDTO: cleanerDTOS){
            Optional<Cleaner> cleanerOptional = cleanerRepository.findById(cleanerDTO.getClrId());
            if(cleanerOptional.isEmpty()){
                throw new EntityNotFoundException(String.format("Мойщика с id=%d не существует в базе данных", cleanerDTO.getClrId()));
            }else{
                Cleaner cleaner = cleanerOptional.get();
                Box box = cleanerDTO.getBox();
                for(WorkScheduleDTO workSchedule: cleanerDTO.getWorkSchedules()){
                    WorkSchedule existsWorkSchedule = cleaner.getWorkSchedules().stream()
                            .filter(w->w.getWsWorkDay().equals(workSchedule.getWsWorkDay()))
                            .findFirst()
                            .orElse(null);

                    if(existsWorkSchedule != null){
                        if(existsWorkSchedule.getWsWorkDay().equals(LocalDate.now())) {
                            conflictMessage += String.format("%s %s %s %s работает в боксе %d \n", existsWorkSchedule.getCleaner().getClrSurname(), existsWorkSchedule.getCleaner().getClrName(), existsWorkSchedule.getCleaner().getClrPatronymic() == null ? ""
                                    : existsWorkSchedule.getCleaner().getClrPatronymic(), existsWorkSchedule.getWsWorkDay().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), existsWorkSchedule.getBox().getBoxId());
                        }else{
                            conflictMessage += String.format("%s %s %s %s будет работать в боксе %d \n", existsWorkSchedule.getCleaner().getClrSurname(), existsWorkSchedule.getCleaner().getClrName(), existsWorkSchedule.getCleaner().getClrPatronymic() == null ? ""
                                    : existsWorkSchedule.getCleaner().getClrPatronymic(), existsWorkSchedule.getWsWorkDay().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), existsWorkSchedule.getBox().getBoxId());
                        }
                    }else {
                        WorkSchedule createdWorkScheduleItem = new WorkSchedule();
                        createdWorkScheduleItem.setWsWorkDay(workSchedule.getWsWorkDay());
                        createdWorkScheduleItem.setCleaner(cleaner);
                        createdWorkScheduleItem.setBox(box);

                        createdWorkScheduleItems.add(createdWorkScheduleItem);
                    }
                }
            }
        }
        workScheduleRepository.saveAll(createdWorkScheduleItems);
        if(!conflictMessage.isEmpty()){
            conflictMessage = "Были пропущены следующие рабочие дни по причине: " + conflictMessage;
        }

        return new ResultCreateWorkSchedulesDTO(conflictMessage, createdWorkScheduleItems);
    }

    public void delete(List<WorkSchedule> workSchedules) {
        workScheduleRepository.deleteAll(workSchedules);
    }
}
