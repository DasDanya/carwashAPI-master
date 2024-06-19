package ru.pin120.carwashAPI.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pin120.carwashAPI.Exceptions.FileIsNotImageException;
import ru.pin120.carwashAPI.dtos.BookingDTO;
import ru.pin120.carwashAPI.dtos.BookingsInfoDTO;
import ru.pin120.carwashAPI.models.Booking;
import ru.pin120.carwashAPI.models.BookingStatus;
import ru.pin120.carwashAPI.models.Cleaner;
import ru.pin120.carwashAPI.services.BookingService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST контроллер, обрабатывающий HTTP-запросы для работы с данными о заказах
 */
//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    /**
     * Сервис для работы с заказами
     */
    private final BookingService bookingService;
    /**
     * Сервис для валидации входных данных
     */
    private final ValidateInputService validateInputService;

    /**
     * Конструктор для внедрения зависимостей
     * @param bookingService сервис для работы с заказами
     * @param validateInputService сервис для валидации входных данных
     */
    public BookingController(BookingService bookingService, ValidateInputService validateInputService) {
        this.bookingService = bookingService;
        this.validateInputService = validateInputService;
    }

    /**
     * Метод, обрабатывающий GET запрос на получение заказов определенного бокса в указанном интервале времени
     * @param startInterval начало временного интервала
     * @param endInterval конец временного интервала
     * @param boxId id бокса
     * @return ResponseEntity со списком заказов и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/boxBookings")
    public ResponseEntity<?> getBoxBookings(@RequestParam("startInterval")LocalDateTime startInterval,
                                            @RequestParam("endInterval") LocalDateTime endInterval,
                                            @RequestParam("boxId") Long boxId)
    {
        try{
            List<Booking> bookings = bookingService.getBoxBookings(startInterval, endInterval, boxId);
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий GET запрос на получение заказов с учётом пагинации
     * @param cleanerId id мойщика
     * @param clientId id клиента
     * @param boxId id бокса
     * @param pageIndex индекс страницы
     * @param startInterval начало временного интервала
     * @param endInterval конец временного интервала
     * @param bookingStatus статус заказа
     * @param compareOperator оператор сравнения стоимости
     * @param price стоимость
     * @return ResponseEntity со списком заказов и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping
    public ResponseEntity<?> get(@RequestParam(value = "cleanerId", required = false) Long cleanerId,
                                               @RequestParam(value = "clientId",required = false) Long clientId,
                                               @RequestParam(value = "boxId",required = false) Long boxId,
                                               @RequestParam(value = "pageIndex") Integer pageIndex,
                                               @RequestParam(value = "startInterval",required = false)LocalDateTime startInterval,
                                               @RequestParam(value = "endInterval",required = false) LocalDateTime endInterval,
                                               @RequestParam(value = "status",required = false) BookingStatus bookingStatus,
                                               @RequestParam(value = "operator",required = false) String compareOperator,
                                               @RequestParam(value = "price",required = false) Integer price)
    {
        try{
            List<Booking> bookings = bookingService.getClientBookings(pageIndex, cleanerId,clientId, boxId, startInterval, endInterval, bookingStatus, compareOperator, price);
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий GET запрос на получение информации об общем количестве и стоимости заказов
     * @param cleanerId id мойщика
     * @param clientId id клиента
     * @param boxId id бокса
     * @param startInterval начало временного интервала
     * @param endInterval конец временного интервала
     * @param bookingStatus статус
     * @param compareOperator заказа
     * @param price оператор сравнения заказов
     * @return ResponseEntity со списком заказов и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/getInfo")
    public ResponseEntity<?> getInfo(@RequestParam(value = "cleanerId", required = false) Long cleanerId,
                                    @RequestParam(value = "clientId",required = false) Long clientId,
                                    @RequestParam(value = "boxId",required = false) Long boxId,
                                    @RequestParam(value = "startInterval",required = false)LocalDateTime startInterval,
                                    @RequestParam(value = "endInterval",required = false) LocalDateTime endInterval,
                                    @RequestParam(value = "status",required = false) BookingStatus bookingStatus,
                                    @RequestParam(value = "operator",required = false) String compareOperator,
                                    @RequestParam(value = "price",required = false) Integer price)
    {
        try{
            BookingsInfoDTO bookingsInfoDTO = bookingService.getBookingsInfo(cleanerId, clientId, boxId, startInterval, endInterval, bookingStatus, compareOperator, price);
            return new ResponseEntity<>(bookingsInfoDTO, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий GET запрос на получение данных о работе мойщика
     * @param cleanerId id мойщика
     * @param startInterval начало временного интервала
     * @param endInterval конец временного интервала
     * @return ResponseEntity со списком заказов и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/getInfoAboutWorkOfCleaner")
    public ResponseEntity<?> getInfoAboutWorkOfCleaner(@RequestParam(value = "cleanerId") Long cleanerId,
                                                       @RequestParam(value = "startInterval",required = false)LocalDateTime startInterval,
                                                       @RequestParam(value = "endInterval",required = false) LocalDateTime endInterval)
    {
        try{
            Map<LocalDate, BookingsInfoDTO> info = bookingService.infoAboutWorkOfCleaner(cleanerId,startInterval,endInterval);
            return new ResponseEntity<>(info, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий POST запрос на добавление заказа
     * @param booking заказ
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @return ResponseEntity c созданным заказом и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid BookingDTO booking, BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            List<Booking> getCrossedBookings = bookingService.getCrossedBookings(booking.getBkStartTime(), booking.getBkEndTime(), booking.getBox().getBoxId());
            if(!getCrossedBookings.isEmpty()){
                return new ResponseEntity<>("Формируемый заказ пересекается по времени с заказом " + getCrossedBookings.get(0).getBkId(), HttpStatus.BAD_REQUEST);
            }else{
                Booking createdBooking = bookingService.create(booking);
                return new ResponseEntity<>(createdBooking, HttpStatus.OK);
            }
        }catch (Exception e){
            if(e instanceof IllegalFormatException){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий PUT запрос на изменение статуса заказа
     * @param id id заказа
     * @param bookingDTO данные об изменяемом заказе
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @return ResponseEntity с заказом, у которого сменился статус, и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
    @PutMapping("/newStatus/{id}")
    public ResponseEntity<?> newStatus(@PathVariable("id") String id, @RequestBody @Valid BookingDTO bookingDTO, BindingResult bindingResult){
        try{
            Optional<Booking> bookingOptional = bookingService.getByBkId(id);
            if(bookingOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Заказ №%s не существует в базе данных",id), HttpStatus.BAD_REQUEST);
            }
            Booking existedBooking = bookingOptional.get();
            if(!existedBooking.getBkId().equals(id)){
                return new ResponseEntity<>("Параметр (номер заказа) не совпадает с номером заказа", HttpStatus.BAD_REQUEST);
            }
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            bookingService.newStatus(bookingDTO, existedBooking);
            return new ResponseEntity<>(existedBooking, HttpStatus.OK);

        }catch (Exception e){
            if(e instanceof IllegalFormatException){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий PUT запрос на изменение данных о заказе
     * @param id id заказа
     * @param booking заказ с новыми данными
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @return ResponseEntity с измененным заказом и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable("id") String id, @RequestBody @Valid BookingDTO booking, BindingResult bindingResult){
        try{
            Optional<Booking> bookingOptional = bookingService.getByBkId(id);
            if(bookingOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Заказ №%s не существует в базе данных",id), HttpStatus.BAD_REQUEST);
            }
            Booking existedBooking = bookingOptional.get();
            if(!existedBooking.getBkId().equals(id)){
                return new ResponseEntity<>("Параметр (номер заказа) не совпадает с номером заказа", HttpStatus.BAD_REQUEST);
            }
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            bookingService.edit(existedBooking, booking);
            return new ResponseEntity<>(existedBooking, HttpStatus.OK);

        }catch (Exception e){
            if(e instanceof FileIsNotImageException || e instanceof IllegalArgumentException){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }else {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    /**
     * Метод, обрабатывающий DELETE запрос на удаление заказа
     * @param id id заказа
     * @return ResponseEntity с сообщением и статус-кодом
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id){
        try{
            Optional<Booking> bookingOptional = bookingService.getByBkId(id);
            if(bookingOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Заказ №%s не существует в базе данных",id), HttpStatus.BAD_REQUEST);
            }

            Booking existedBooking = bookingOptional.get();
            if(existedBooking.getBkStatus() == BookingStatus.NOT_DONE || existedBooking.getBkStatus() == BookingStatus.CANCELLED){
                bookingService.delete(existedBooking);
                return ResponseEntity.noContent().build();
            }else{
                return new ResponseEntity<>("Можно удалить заказ со статусом Отменен или Не выполнен", HttpStatus.BAD_REQUEST);
            }

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
