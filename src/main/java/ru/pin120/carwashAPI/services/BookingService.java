package ru.pin120.carwashAPI.services;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pin120.carwashAPI.dtos.BookingDTO;
import ru.pin120.carwashAPI.dtos.ServiceWithPriceListDTO;
import ru.pin120.carwashAPI.models.Booking;
import ru.pin120.carwashAPI.models.BookingStatus;
import ru.pin120.carwashAPI.models.BoxStatus;
import ru.pin120.carwashAPI.repositories.BookingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingIdSequenceService bookingIdSequenceService;
    private final ServService servService;

    private final LocalTime START_WORK_TIME = LocalTime.of(8,0);
    private final LocalTime END_WORK_TIME = LocalTime.of(20, 0);

    public BookingService(BookingRepository bookingRepository, BookingIdSequenceService bookingIdSequenceService, ServService servService) {
        this.bookingRepository = bookingRepository;
        this.bookingIdSequenceService = bookingIdSequenceService;
        this.servService = servService;
    }


    public List<Booking> getBoxBookings(LocalDateTime startInterval, LocalDateTime endInterval, Long boxId){
        return bookingRepository.getBoxBookings(startInterval, endInterval, boxId);
    }

    @Transactional
    public Booking create(@Valid BookingDTO booking) {
        LocalDate now = LocalDate.now();
        if(booking.getBkStartTime().toLocalDate().isBefore(now)){
            throw new IllegalArgumentException("Время начала выполнения заказа не может быть раньше " + now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }
        if(booking.getBkEndTime().toLocalDate().isBefore(now)){
            throw new IllegalArgumentException("Время окончания выполнения заказа не может быть раньше " + now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }
        LocalTime bkEndTime = LocalTime.of(booking.getBkEndTime().getHour(), booking.getBkEndTime().getMinute());
        LocalTime bkStartTime = LocalTime.of(booking.getBkStartTime().getHour(), booking.getBkStartTime().getMinute());
        if(bkStartTime.isBefore(START_WORK_TIME)){
            throw new IllegalArgumentException("Время начала заказа не может быть раньше " + START_WORK_TIME);
        }
        if(bkEndTime.isBefore(START_WORK_TIME)){
            throw new IllegalArgumentException("Время окончания заказа не может быть раньше " + START_WORK_TIME);
        }
        if(!bkStartTime.isBefore(END_WORK_TIME)){
            throw new IllegalArgumentException("Время начала заказа не может быть позднее " + END_WORK_TIME.minusMinutes(1));
        }
        if(bkEndTime.isAfter(END_WORK_TIME)){
            throw new IllegalArgumentException("Время окончания заказа не может быть позднее " + END_WORK_TIME);
        }
        if(booking.getBox().getBoxStatus() == BoxStatus.CLOSED){
            throw new IllegalArgumentException(String.format("Нельзя сформировать заказ, так как бокс %d закрыт", booking.getBox().getBoxId()));
        }

        Booking createdBooking = new Booking();
        createdBooking.setBkStartTime(booking.getBkStartTime());
        createdBooking.setBkEndTime(booking.getBkEndTime());
        createdBooking.setClientTransport(booking.getClientTransport());
        createdBooking.setBox(booking.getBox());
        createdBooking.setBkStatus(BookingStatus.BOOKED);
        List<Booking> otherBookingsInSameTime = bookingRepository.notNegativeBookingsOfTransportInIntervalTimeWithoutCurrentBox(createdBooking.getBkStartTime(), createdBooking.getBkEndTime(), createdBooking.getClientTransport().getClTrStateNumber(), Arrays.asList(BookingStatus.NOT_DONE, BookingStatus.CANCELLED_BY_CLIENT));
        if(!otherBookingsInSameTime.isEmpty()){
            Booking otherBookingInSameTime = otherBookingsInSameTime.get(0);
            throw new IllegalArgumentException(String.format("Нельзя сформировать заказ, так как он пересекается по времени с заказом %s (бокс №%d, время начала %s, время окончания %s ), " +
                    "у которого указан транспорт с гос.номером %s и статус входит в следующий список: бронь, выполняется, выполнен, не выполнен",otherBookingInSameTime.getBkId(), otherBookingInSameTime.getBox().getBoxId(), otherBookingInSameTime.getBkStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), otherBookingInSameTime.getBkEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), otherBookingInSameTime.getClientTransport().getClTrStateNumber()));
        }

        int price = 0;
        createdBooking.setServices(new ArrayList<>());
        for (ServiceWithPriceListDTO serviceDTO: booking.getServices()){
            Optional<ru.pin120.carwashAPI.models.Service> serviceOptional = servService.getByServName(serviceDTO.getServName());
            if(serviceOptional.isEmpty()){
                throw new IllegalArgumentException("В базе данных отсутствует услуга " + serviceDTO.getServName());
            }else{
                createdBooking.getServices().add(serviceOptional.get());
                price+=serviceDTO.getPlPrice();
            }
        }
        createdBooking.setBkPrice(calculatePrice(price, createdBooking.getClientTransport().getClient().getClDiscount()));
        createdBooking.setBkId(bookingIdSequenceService.generateId());
        bookingRepository.save(createdBooking);

        return createdBooking;
    }

    public List<Booking> getCrossedBookings(LocalDateTime startInterval, LocalDateTime endInterval, Long boxId){
        startInterval = startInterval.withSecond(0).withNano(0);
        endInterval = endInterval.withSecond(0).withNano(0);

        return bookingRepository.getCrossedBookings(startInterval, endInterval, boxId, Arrays.asList(BookingStatus.BOOKED, BookingStatus.IN_PROGRESS, BookingStatus.DONE));
    }

    public int calculatePrice(int price, int discount){
        double discountedPrice = price * (1 - (discount / 100.0));
        return (int) Math.floor(discountedPrice);
    }
}
