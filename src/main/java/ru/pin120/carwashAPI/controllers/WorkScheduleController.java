package ru.pin120.carwashAPI.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.carwashAPI.dtos.CleanerDTO;
import ru.pin120.carwashAPI.dtos.ResultCreateWorkSchedulesDTO;
import ru.pin120.carwashAPI.models.WorkSchedule;
import ru.pin120.carwashAPI.services.ValidateInputService;
import ru.pin120.carwashAPI.services.WorkScheduleService;

import java.time.LocalDate;
import java.util.List;


/**
 * REST контроллер, обрабатывающий HTTP-запросы для работы с данными о рабочих днях мойщиков
 */
//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/workSchedule")
public class WorkScheduleController {

    /**
     * Сервис для работы с рабочими днями мойщиков
     */
    private final WorkScheduleService workScheduleService;

    /**
     * Сервис для валидации входных данных
     */
    private final ValidateInputService validateInputService;

    /**
     * Конструктор для внедрения зависимостей
     * @param workScheduleService сервис для работы с рабочими днями мойщиков
     * @param validateInputService сервис для валидации входных данных
     */
    public WorkScheduleController(WorkScheduleService workScheduleService, ValidateInputService validateInputService) {
        this.workScheduleService = workScheduleService;
        this.validateInputService = validateInputService;
    }


    /**
     * Метод, обрабатывающий GET запрос на получение списка рабочих дней мойщика
     * @param startInterval начало временного интервала
     * @param endInterval конец временного интервала
     * @param clrId id мойщика
     * @param pageIndex индекс страницы
     * @return ResponseEntity со списком рабочих дней мойщика и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping
    public ResponseEntity<List<WorkSchedule>> get(@RequestParam(value = "startInterval") LocalDate startInterval,
                                                  @RequestParam(value = "endInterval") LocalDate endInterval,
                                                  @RequestParam(value = "clrId") Long clrId,
                                                  @RequestParam(value = "pageIndex", required = false) Integer pageIndex)
    {
        try{
            List<WorkSchedule> workSchedules = workScheduleService.get(startInterval, endInterval, clrId,pageIndex);
            return new ResponseEntity<>(workSchedules, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий POST запрос на добавление рабочего дня
     * @param cleanerDTOS список мойщиков с их рабочими днями
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @return ResponseEntity с добавленными рабочими днями и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid List<CleanerDTO> cleanerDTOS, BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }
            ResultCreateWorkSchedulesDTO result = workScheduleService.create(cleanerDTOS);
            if(result.getConflictMessage().isEmpty()) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(result, HttpStatus.CONFLICT);
            }

        }catch (Exception e){
            if(e instanceof EntityNotFoundException || e instanceof IllegalArgumentException){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий DELETE запрос на удаление рабочих дней
     * @param workSchedules список удаляемых рабочих дней
     * @return ResponseEntity с сообщением и статус-кодом
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody List<WorkSchedule> workSchedules){
        try{
            workScheduleService.delete(workSchedules);
            return ResponseEntity.noContent().build();

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
