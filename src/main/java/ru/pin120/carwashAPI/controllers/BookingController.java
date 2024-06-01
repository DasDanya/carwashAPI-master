package ru.pin120.carwashAPI.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.carwashAPI.dtos.BookingDTO;
import ru.pin120.carwashAPI.models.Booking;
import ru.pin120.carwashAPI.services.BookingService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.time.LocalDateTime;
import java.util.IllegalFormatException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final ValidateInputService validateInputService;

    public BookingController(BookingService bookingService, ValidateInputService validateInputService) {
        this.bookingService = bookingService;
        this.validateInputService = validateInputService;
    }

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

}
