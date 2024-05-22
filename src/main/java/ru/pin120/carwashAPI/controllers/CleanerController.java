package ru.pin120.carwashAPI.controllers;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pin120.carwashAPI.Exceptions.FileIsNotImageException;
import ru.pin120.carwashAPI.dtos.CleanerDTO;
import ru.pin120.carwashAPI.models.Cleaner;
import ru.pin120.carwashAPI.models.CleanerStatus;
import ru.pin120.carwashAPI.models.WorkSchedule;
import ru.pin120.carwashAPI.services.CleanerService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cleaners")
public class CleanerController {

    private final CleanerService cleanerService;
    private final ValidateInputService validateInputService;

    public CleanerController(CleanerService cleanerService, ValidateInputService validateInputService) {
        this.cleanerService = cleanerService;
        this.validateInputService = validateInputService;
    }

    @GetMapping
    public ResponseEntity<List<Cleaner>> get(
            @RequestParam(value = "surname",required = false) String surname,
            @RequestParam(value = "name",required = false) String name,
            @RequestParam(value = "patronymic",required = false) String patronymic,
            @RequestParam(value = "phone",required = false) String phone,
            @RequestParam(value = "status",required = false) CleanerStatus status,
            @RequestParam(value = "boxNumber",required = false) Long boxNumber)
    {

        try{
            List<Cleaner> cleaners = cleanerService.get(surname, name,patronymic,phone,status,boxNumber);
            return new ResponseEntity<>(cleaners, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/workSchedule")
    public ResponseEntity<?> getWithWorkSchedule(@RequestParam(value = "startInterval") LocalDate startInterval,
                                                                @RequestParam(value = "endInterval") LocalDate endInterval,
                                                                @RequestParam(value = "currentMonth") boolean currentMonth)
    {
        try{
            if(!endInterval.isAfter(startInterval)){
                return new ResponseEntity<>("Дата окончания интервала должна быть больше даты начала", HttpStatus.BAD_REQUEST);
            }
            List<CleanerDTO> cleanerDTOS = cleanerService.getCleanersWithWorkSchedule(startInterval, endInterval, currentMonth);

            return new ResponseEntity<>(cleanerDTOS, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getPhoto/{photoName}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable("photoName")String photoName){
        try{
            //ByteSenderDTO byteSenderDTO = new ByteSenderDTO(cleanerService.getPhoto(photoName));
            String photoBase64 = Base64.getEncoder().encodeToString(cleanerService.getPhoto(photoName));
            return new ResponseEntity<>(cleanerService.getPhoto(photoName), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestPart @Valid Cleaner cleaner, BindingResult bindingResult, @RequestPart(required = false) MultipartFile photo){
        try{
            if(bindingResult.hasErrors()){
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }
            cleanerService.create(cleaner, photo);
            return new ResponseEntity<>(cleaner, HttpStatus.OK);

        }catch (Exception e){
            if(e instanceof FileIsNotImageException){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }else {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable("id") Long id, @RequestPart @Valid Cleaner cleaner, BindingResult bindingResult, @RequestPart(required = false) MultipartFile photo){
        try{
            System.out.println("edit");
            Optional<Cleaner> cleanerOptional = cleanerService.getById(id);
            if(cleanerOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Мойщик с id = %d не существует в базе данных",id), HttpStatus.BAD_REQUEST);
            }
            Cleaner existedCleaner = cleanerOptional.get();
            if(existedCleaner.getClrId().longValue() != cleaner.getClrId().longValue()){
                return new ResponseEntity<>("Параметр id не совпадает с id мойщика", HttpStatus.BAD_REQUEST);
            }
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            cleanerService.edit(existedCleaner, cleaner, photo);

            return new ResponseEntity<>(existedCleaner, HttpStatus.OK);

        }catch (Exception e){
            if(e instanceof FileIsNotImageException){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }else {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        try{
            Optional<Cleaner> cleanerOptional = cleanerService.getById(id);
            if(cleanerOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Мойщик с id = %d не существует в базе данных", id),HttpStatus.BAD_REQUEST);
            }

            Cleaner existedCleaner = cleanerOptional.get();
            // условие, что нельзя удалять мойщика
//            if(!existedTransport.getBookings().isEmpty()){
//                return new ResponseEntity<>(String.format("Нельзя удалить транспорт %s %s, так как он указан в заказе", existedTransport.getTransport().getTrMark(), existedTransport.getTransport().getTrModel()), HttpStatus.BAD_REQUEST);
//            }

            cleanerService.delete(existedCleaner);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.noContent().build();
    }
}
