package ru.pin120.carwashAPI.controllers;


import jakarta.persistence.EntityNotFoundException;
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

/**
 * REST контроллер, обрабатывающий HTTP-запросы для работы с данными о мойщиках
 */
//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cleaners")
public class CleanerController {

    /**
     * Сервис для работы с мойщиками
     */
    private final CleanerService cleanerService;

    /**
     * Сервис для валидации входных данных
     */
    private final ValidateInputService validateInputService;


    /**
     * Конструктор для внедрения зависимостей
     * @param cleanerService сервис для работы с мойщиками
     * @param validateInputService сервис для валидации входных данных
     */
    public CleanerController(CleanerService cleanerService, ValidateInputService validateInputService) {
        this.cleanerService = cleanerService;
        this.validateInputService = validateInputService;
    }

    /**
     * Метод, обрабатывающий GET запрос на получение списка мойщиков
     * @param surname фамилия мойщика
     * @param name имя мойщика
     * @param patronymic отчество мойщика
     * @param phone номер телефона
     * @param status статус
     * @return ResponseEntity со списком мойщиков и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping
    public ResponseEntity<?> get(
            @RequestParam(value = "surname",required = false) String surname,
            @RequestParam(value = "name",required = false) String name,
            @RequestParam(value = "patronymic",required = false) String patronymic,
            @RequestParam(value = "phone",required = false) String phone,
            @RequestParam(value = "status",required = false) CleanerStatus status)
    {

        try{
            List<Cleaner> cleaners = cleanerService.get(surname, name,patronymic,phone,status);
            return new ResponseEntity<>(cleaners, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий GET запрос на получение рабочих дней мойщика
     * @param startInterval начало временного интервала
     * @param endInterval конец временного интервала
     * @param boxId id бокса
     * @param currentMonth текущий ли месяц
     * @return ResponseEntity со списком рабочих дней мойщика и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
    @GetMapping("/workSchedule")
    public ResponseEntity<?> getWithWorkSchedule(@RequestParam(value = "startInterval") LocalDate startInterval,
                                                                @RequestParam(value = "endInterval") LocalDate endInterval,
                                                                @RequestParam(value = "boxId") Long boxId,
                                                                @RequestParam(value = "currentMonth") boolean currentMonth)
    {
        try{
            if(!endInterval.isAfter(startInterval)){
                return new ResponseEntity<>("Дата окончания интервала должна быть больше даты начала", HttpStatus.BAD_REQUEST);
            }
            List<CleanerDTO> cleanerDTOS = cleanerService.getCleanersWithWorkSchedule(startInterval, endInterval, boxId, currentMonth);

            return new ResponseEntity<>(cleanerDTOS, HttpStatus.OK);
        }catch (Exception e){
            if(e instanceof EntityNotFoundException){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий GET запрос на получение фотографии мойщика
     * @param photoName название фотографии
     * @return ResponseEntity с фотографией мойщика в виде массива байт и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/getPhoto/{photoName}")
    public ResponseEntity<?> getPhoto(@PathVariable("photoName")String photoName){
        try{
            //ByteSenderDTO byteSenderDTO = new ByteSenderDTO(cleanerService.getPhoto(photoName));
            String photoBase64 = Base64.getEncoder().encodeToString(cleanerService.getPhoto(photoName));
            return new ResponseEntity<>(cleanerService.getPhoto(photoName), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий POST запрос на добавление мойщика
     * @param cleaner мойщик
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @param photo фотография мойщика
     * @return ResponseEntity с добавленным мойщиком и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
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

    /**
     * Метод, обрабатывающий PUT запрос на изменение данных о мойщике
     * @param id id мойщика
     * @param cleaner мойщик с новыми данными
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @param photo фотография
     * @return ResponseEntity с измененными данными о мойщике и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable("id") Long id, @RequestPart @Valid Cleaner cleaner, BindingResult bindingResult, @RequestPart(required = false) MultipartFile photo){
        try{
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
            if(e instanceof FileIsNotImageException || e instanceof IllegalArgumentException){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }else {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    /**
     * Метод, обрабатывающий DELETE запрос на удаление мойщика
     * @param id id мойщика
     * @return ResponseEntity с сообщением и статус-кодом
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        try{
            Optional<Cleaner> cleanerOptional = cleanerService.getById(id);
            if(cleanerOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Мойщик с id = %d не существует в базе данных", id),HttpStatus.BAD_REQUEST);
            }

            Cleaner existedCleaner = cleanerOptional.get();
            if(!existedCleaner.getBookings().isEmpty()){
                return new ResponseEntity<>(String.format("Нельзя удалить мойщика %s %s %s, так как он указан в заказе", existedCleaner.getClrSurname(), existedCleaner.getClrName(), existedCleaner.getClrPatronymic() == null ? "" : existedCleaner.getClrPatronymic()), HttpStatus.BAD_REQUEST);
            }

            cleanerService.delete(existedCleaner);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.noContent().build();
    }
}
