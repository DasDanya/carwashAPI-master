package ru.pin120.carwashAPI.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pin120.carwashAPI.dtos.CleanerDTO;
import ru.pin120.carwashAPI.dtos.ResultCreateWorkSchedulesDTO;
import ru.pin120.carwashAPI.dtos.WorkScheduleDTO;
import ru.pin120.carwashAPI.models.Box;
import ru.pin120.carwashAPI.models.Cleaner;
import ru.pin120.carwashAPI.models.CleanerStatus;
import ru.pin120.carwashAPI.models.WorkSchedule;
import ru.pin120.carwashAPI.repositories.CleanerRepository;
import ru.pin120.carwashAPI.repositories.WorkScheduleRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис рабочих дней
 */
@Service
public class WorkScheduleService {

    /**
     * Репозиторий рабочих дней
     */
    private final WorkScheduleRepository workScheduleRepository;

    /**
     * Репозиторий мойщиков
     */
    private final CleanerRepository cleanerRepository;

    @Autowired
    private Environment environment;

    /**
     * Внедрение зависимостей
     * @param workScheduleRepository репозиторий рабочих дней
     * @param cleanerRepository репозиторий мойщиков
     */
    public WorkScheduleService(WorkScheduleRepository workScheduleRepository, CleanerRepository cleanerRepository) {
        this.workScheduleRepository = workScheduleRepository;
        this.cleanerRepository = cleanerRepository;
    }

    /**
     * Удаление рабочих дней мойщика после определенного для
     * @param clrId id мойщика
     * @param start день, с которого начинается удаление
     */
    public void deleteByClrIdAndStartDate(Long clrId, LocalDate start){
        workScheduleRepository.deleteByCleanerIdAndWsWorkDayAfterOrEqual(clrId, start);
    }

    /**
     * Получение рабочих дней
     * @param startInterval начало временного интервала
     * @param endInterval конец временного интервала
     * @param clrId id мойщика
     * @param pageIndex индекс страницы
     * @return Список рабочих дней
     */
    public List<WorkSchedule> get(LocalDate startInterval, LocalDate endInterval, Long clrId, Integer pageIndex){
        if(pageIndex != null) {
            Pageable pageable = PageRequest.of(pageIndex, Integer.parseInt(environment.getProperty("COUNT_ITEMS_IN_PAGE_WORKSCHEDULE")), Sort.by("wsWorkDay"));
            return workScheduleRepository.get(startInterval, endInterval, clrId, pageable);
        }else{
            return workScheduleRepository.get(startInterval,endInterval,clrId);
        }
    }

    /**
     * Преобразование WorkSchedule в DTO
     * @param workSchedule рабочий день
     * @return DTO
     */
    public static WorkScheduleDTO toDTO(WorkSchedule workSchedule){
        return new WorkScheduleDTO(
                workSchedule.getWsId(),
                workSchedule.getWsWorkDay()
        );
    }

    /**
     * Создание рабочего дня
     * @param cleanerDTOS список мойщиков с их рабочими днями
     * @return Результат создания рабочих дней
     */
    public ResultCreateWorkSchedulesDTO create(List<CleanerDTO> cleanerDTOS) {
        List<WorkSchedule> createdWorkScheduleItems = new ArrayList<>();
        String conflictMessage = "";
        for(CleanerDTO cleanerDTO: cleanerDTOS){
            Optional<Cleaner> cleanerOptional = cleanerRepository.findById(cleanerDTO.getClrId());
            if(cleanerOptional.isEmpty()){
                throw new EntityNotFoundException(String.format("Мойщика с id=%d не существует в базе данных", cleanerDTO.getClrId()));
            }else{
                Cleaner cleaner = cleanerOptional.get();
                if(cleaner.getClrStatus() == CleanerStatus.ACT) {
                    Box box = cleanerDTO.getBox();
                    for (WorkScheduleDTO workSchedule : cleanerDTO.getWorkSchedules()) {
                        WorkSchedule existsWorkSchedule = cleaner.getWorkSchedules().stream()
                                .filter(w -> w.getWsWorkDay().equals(workSchedule.getWsWorkDay()))
                                .findFirst()
                                .orElse(null);

                        if (existsWorkSchedule != null) {
                            if (existsWorkSchedule.getWsWorkDay().equals(LocalDate.now())) {
                                conflictMessage += String.format("%s %s %s %s работает в боксе %d \n", existsWorkSchedule.getCleaner().getClrSurname(), existsWorkSchedule.getCleaner().getClrName(), existsWorkSchedule.getCleaner().getClrPatronymic() == null ? ""
                                        : existsWorkSchedule.getCleaner().getClrPatronymic(), existsWorkSchedule.getWsWorkDay().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), existsWorkSchedule.getBox().getBoxId());
                            } else {
                                conflictMessage += String.format("%s %s %s %s будет работать в боксе %d \n", existsWorkSchedule.getCleaner().getClrSurname(), existsWorkSchedule.getCleaner().getClrName(), existsWorkSchedule.getCleaner().getClrPatronymic() == null ? ""
                                        : existsWorkSchedule.getCleaner().getClrPatronymic(), existsWorkSchedule.getWsWorkDay().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), existsWorkSchedule.getBox().getBoxId());
                            }
                        } else {
                            WorkSchedule createdWorkScheduleItem = new WorkSchedule();
                            createdWorkScheduleItem.setWsWorkDay(workSchedule.getWsWorkDay());
                            createdWorkScheduleItem.setCleaner(cleaner);
                            createdWorkScheduleItem.setBox(box);

                            createdWorkScheduleItems.add(createdWorkScheduleItem);
                        }
                    }
                }else{
                    throw new IllegalArgumentException("Нельзя устанавливать график работы уволенному мойщику");
                }
            }
        }
        workScheduleRepository.saveAll(createdWorkScheduleItems);
        if(!conflictMessage.isEmpty()){
            conflictMessage = "Были пропущены следующие рабочие дни по причине: " + conflictMessage;
        }

        return new ResultCreateWorkSchedulesDTO(conflictMessage, createdWorkScheduleItems);
    }

    /**
     * Удаление рабочих дней
     * @param workSchedules список рабочих дней
     */
    public void delete(List<WorkSchedule> workSchedules) {
        workScheduleRepository.deleteAll(workSchedules);
    }
}
