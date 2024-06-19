package ru.pin120.carwashAPI.controllers;

import jakarta.servlet.ServletContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.carwashAPI.models.*;
import ru.pin120.carwashAPI.services.BoxService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.util.List;
import java.util.Optional;



/**
 * REST контроллер, обрабатывающий HTTP-запросы для работы с данными о боксах
 */
//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/boxes")
public class BoxController {

    /**
     * Сервис для работы с боксами
     */
    private final BoxService boxService;

    /**
     * Сервис для валидации входных данных
     */
    private final ValidateInputService validateInputService;


    /**
     * Конструктор для внедрения зависимостей
     * @param boxService сервис для работы с боксами
     * @param validateInputService сервис для валидации входных данных
     */
    public BoxController(BoxService boxService, ValidateInputService validateInputService) {
        this.boxService = boxService;
        this.validateInputService = validateInputService;
    }

    /**
     * Метод, обрабатывающий GET запрос на получение всех боксов
     * @return ResponseEntity со списком боксов и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping
    public ResponseEntity<?> getAll(){
        try{
            List<Box> boxes = boxService.getAll();
            return new ResponseEntity<>(boxes, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий GET запрос на получение доступных боксов
     * @return ResponseEntity со списком заказов и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/available")
    public ResponseEntity<List<Box>> getAvailable(){
        try{
            List<Box> availableBoxes = boxService.getAvailable();
            return new ResponseEntity<>(availableBoxes, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Метод, обрабатывающий POST запрос на добавление бокса
     * @param box бокс
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @return ResponseEntity с добавленным боксом и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid Box box, BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            boxService.save(box);
            return new ResponseEntity<>(box, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий PUT запрос на изменение данных о боксе
     * @param id id бокса
     * @param box бокс с новыми данными
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @return ResponseEntity с измененным боксом и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable("id") Long id, @RequestBody @Valid Box box, BindingResult bindingResult){
        try{
            Optional<Box> boxOptional = boxService.getById(id);
            if(boxOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Бокс с номером = %d не существует в базе данных",id), HttpStatus.BAD_REQUEST);
            }
            Box existedBox = boxOptional.get();
            if(existedBox.getBoxId().longValue() != box.getBoxId().longValue()){
                return new ResponseEntity<>("Параметр (номер) не совпадает с номером бокса", HttpStatus.BAD_REQUEST);
            }
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            if(box.getBoxStatus() == BoxStatus.CLOSED){
                Booking booking = existedBox.getBookings().stream()
                        .filter(b->b.getBkStatus() == BookingStatus.IN_PROGRESS)
                        .findFirst()
                        .orElse(null);
                if(booking != null){
                    return new ResponseEntity<>("Нельзя закрыть бокс, так как в нем выполняется заказ №" + booking.getBkId(), HttpStatus.BAD_REQUEST);
                }
            }

            existedBox.setBoxStatus(box.getBoxStatus());
            boxService.save(existedBox);
            return new ResponseEntity<>(existedBox, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Метод, обрабатывающий DELETE запрос на удаление бокса
     * @param id id бокса
     * @return ResponseEntity с сообщением и статус-кодом
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        try{
            Optional<Box> boxOptional = boxService.getById(id);
            if(boxOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Бокс с номером = %d не существует в базе данных", id),HttpStatus.BAD_REQUEST);
            }

            Box existedBox = boxOptional.get();
            if(!existedBox.getBookings().isEmpty()){
                return new ResponseEntity<>(String.format("Нельзя удалить бокс №%d, так как он указан в заказе", existedBox.getBoxId()), HttpStatus.BAD_REQUEST);
            }

            boxService.delete(existedBox.getBoxId());
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.noContent().build();
    }

}
