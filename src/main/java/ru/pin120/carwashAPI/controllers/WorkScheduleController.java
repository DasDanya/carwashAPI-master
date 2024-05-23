package ru.pin120.carwashAPI.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.carwashAPI.dtos.CleanerDTO;
import ru.pin120.carwashAPI.dtos.WorkScheduleDTO;
import ru.pin120.carwashAPI.models.WorkSchedule;
import ru.pin120.carwashAPI.services.ValidateInputService;
import ru.pin120.carwashAPI.services.WorkScheduleService;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/workSchedule")
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;
    private final ValidateInputService validateInputService;

    public WorkScheduleController(WorkScheduleService workScheduleService, ValidateInputService validateInputService) {
        this.workScheduleService = workScheduleService;
        this.validateInputService = validateInputService;
    }


//    @GetMapping
//    public ResponseEntity<List<WorkSchedule>> get(@RequestParam(value = "startDay")LocalDate startDay, @RequestParam(value = "endDay") LocalDate endDay, @RequestParam(value = "currentMonth") boolean currentMonth){
//        try{
//
//            return new ResponseEntity<>(workSchedules, HttpStatus.OK);
//        }catch (Exception e){
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid List<CleanerDTO> cleanerDTOS, BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }
            List<WorkSchedule> workSchedules = workScheduleService.create(cleanerDTOS);
            return new ResponseEntity<>(workSchedules, HttpStatus.OK);

        }catch (Exception e){
            if(e instanceof EntityNotFoundException){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
