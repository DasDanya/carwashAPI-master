package ru.pin120.carwashAPI.services;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pin120.carwashAPI.dtos.BookingDTO;
import ru.pin120.carwashAPI.dtos.ServiceWithPriceListDTO;
import ru.pin120.carwashAPI.models.*;
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
    private final SuppliesInBoxService suppliesInBoxService;
    private final PriceListService priceListService;

    private final LocalTime START_WORK_TIME = LocalTime.of(8,0);
    private final LocalTime END_WORK_TIME = LocalTime.of(20, 0);

    public BookingService(BookingRepository bookingRepository, BookingIdSequenceService bookingIdSequenceService, ServService servService, SuppliesInBoxService suppliesInBoxService, PriceListService priceListService) {
        this.bookingRepository = bookingRepository;
        this.bookingIdSequenceService = bookingIdSequenceService;
        this.servService = servService;
        this.suppliesInBoxService = suppliesInBoxService;
        this.priceListService = priceListService;
    }


    public List<Booking> getBoxBookings(LocalDateTime startInterval, LocalDateTime endInterval, Long boxId){
        return bookingRepository.getBoxBookings(startInterval, endInterval, boxId);
    }

    @Transactional
    public Booking create(@Valid BookingDTO booking) {
        LocalDate now = LocalDate.now();
        if(!booking.getBkStartTime().isBefore(booking.getBkEndTime())){
            throw new IllegalArgumentException("Время окончания выполнения заказа должно быть позже времени начала выполнения");
        }
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
        List<Booking> otherBookingsInSameTime = bookingRepository.notNegativeBookingsOfTransportInIntervalTime(createdBooking.getBkStartTime(), createdBooking.getBkEndTime(), createdBooking.getClientTransport().getClTrStateNumber(), List.of(BookingStatus.CANCELLED));
        if(!otherBookingsInSameTime.isEmpty()){
            Booking otherBookingInSameTime = otherBookingsInSameTime.get(0);
            throw new IllegalArgumentException(String.format("Нельзя сформировать заказ, так как он пересекается по времени с заказом %s (бокс №%d, время начала %s, время окончания %s ), " +
                    "у которого указан транспорт с гос.номером %s и статус входит в следующий список: бронь, выполняется, выполнен, не выполнен",otherBookingInSameTime.getBkId(), otherBookingInSameTime.getBox().getBoxId(), otherBookingInSameTime.getBkStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), otherBookingInSameTime.getBkEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), otherBookingInSameTime.getClientTransport().getClTrStateNumber()));
        }

        int price = 0;
//        if(booking.getServices().isEmpty()){
//            throw new IllegalArgumentException("Необходимо указать услуги для выполнения");
//        }
        createdBooking.setServices(new ArrayList<>());
        for (ServiceWithPriceListDTO serviceDTO: booking.getServices()){
            Optional<ru.pin120.carwashAPI.models.Service> serviceOptional = servService.getByServName(serviceDTO.getServName());
            if(serviceOptional.isEmpty()){
                throw new IllegalArgumentException("В базе данных отсутствует услуга " + serviceDTO.getServName());
            }else{
                ru.pin120.carwashAPI.models.Service service = serviceOptional.get();
                if(service.getCategoriesOfSupplies().isEmpty()){
                    throw new IllegalArgumentException(String.format("Услуге %s необходимо указать необходимую для выполнения категорию автомоечных средств", service.getServName()));
                }
                createdBooking.getServices().add(service);
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

        return bookingRepository.getCrossedBookings(startInterval, endInterval, boxId, Arrays.asList(BookingStatus.BOOKED, BookingStatus.IN_PROGRESS, BookingStatus.DONE, BookingStatus.NOT_DONE));
    }

    public int calculatePrice(int price, int discount){
        double discountedPrice = price * (1 - (discount / 100.0));
        return (int) Math.floor(discountedPrice);
    }

    public Optional<Booking> getByBkId(String bkId){
        return bookingRepository.findById(bkId);
    }

    @Transactional
    public void newStatus(BookingDTO bookingDTO, Booking existedBooking) {
        switch (bookingDTO.getBkStatus()){
            case CANCELLED:
                if(existedBooking.getBkStatus() != BookingStatus.BOOKED){
                    throw new IllegalArgumentException("Изменить статус на \"Отменен клиентом\" можно только заказу со статусом \"Бронь\"");
                }else{
                    existedBooking.setBkStatus(BookingStatus.CANCELLED);
                }
                break;
            case IN_PROGRESS:
                if(existedBooking.getBkStatus() != BookingStatus.BOOKED){
                    throw new IllegalArgumentException("Изменить статус на \"Выполняется\" можно только заказу со статусом \"Бронь\"");
                }
                else{
                    if(existedBooking.getBox().getBoxStatus() == BoxStatus.CLOSED){
                        throw new IllegalArgumentException("Невозможно начать выполнение заказа, так как закрыт бокс №" + existedBooking.getBox().getBoxId());
                    }
                    LocalDateTime nowTime = LocalDateTime.now().withSecond(0).withNano(0);
                    if(nowTime.isBefore(existedBooking.getBkStartTime()) || nowTime.isAfter(existedBooking.getBkEndTime())) {
                        throw new IllegalArgumentException(String.format("Изменить статус заказа можно только в следующее время: %s-%s", existedBooking.getBkStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), existedBooking.getBkEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))));
                    }

                    List<Booking> notEndedBookings = bookingRepository.findNotEndedBookingsInBox(existedBooking.getBkStartTime(), List.of(BookingStatus.BOOKED, BookingStatus.IN_PROGRESS), existedBooking.getBox().getBoxId());
                    if(!notEndedBookings.isEmpty()){
                        Booking notEndedBooking = notEndedBookings.get(0);
                        String notEndedBookingStatus = notEndedBooking.getBkStatus() == BookingStatus.BOOKED ? "\"Бронь\"" : "\"Выполняется\"";
                        throw new IllegalArgumentException(String.format("Невозможно начать выполнение заказа, так как есть незавершенный заказ, со статусом %s (заказ №%s c интервалом выполнения %s-%s)",notEndedBookingStatus, notEndedBooking.getBkId(), notEndedBooking.getBkStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), notEndedBooking.getBkEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))));
                    }

                    WorkSchedule workSchedule = existedBooking.getBox().getWorkSchedules().stream()
                            .filter(w->w.getWsWorkDay().equals(existedBooking.getBkStartTime().toLocalDate()))
                            .findFirst()
                            .orElse(null);

                    if(workSchedule == null){
                        throw new IllegalArgumentException(String.format("Невозможно начать выполнение заказа, так как в боксе №%d отсутствует мойщик", existedBooking.getBox().getBoxId()));
                    }

                    CategoryOfTransport categoryOfTransport = existedBooking.getClientTransport().getTransport().getCategoryOfTransport();
                    for(ru.pin120.carwashAPI.models.Service service: existedBooking.getServices()){
                        List<CategoryOfSupplies> categoriesOfSupplies = service.getCategoriesOfSupplies();
                        if(!priceListService.existPriceListPosition(categoryOfTransport.getCatTrId(), service.getServName())){
                            throw new IllegalArgumentException(String.format("Нельзя начать выполнение заказа, так как в настоящее время для категории транспорта %s отсутствует возможность выполнения услуги %s", categoryOfTransport.getCatTrName(), service.getServName()));
                        }
                        for (CategoryOfSupplies category : categoriesOfSupplies) {
                            List<SuppliesInBox> suppliesInBox = suppliesInBoxService.getListExistingSuppliesCertainCategory(existedBooking.getBox().getBoxId(), category.getCSupName());
                            if (suppliesInBox.isEmpty()) {
                                throw new IllegalArgumentException("В боксе отсутствует автомоечное средство категории " + category.getCSupName());
                            }
                        }
                    }
                }
                existedBooking.setBkStatus(BookingStatus.IN_PROGRESS);
                break;
            case BOOKED:
                if(existedBooking.getBkStatus() != BookingStatus.CANCELLED) {
                    throw new IllegalArgumentException("Изменить статус на \"Бронь\" можно только заказу со статусом \"Отменен\"");
                }else{
//                    LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
//                    if(now.isAfter(existedBooking.getBkEndTime())){
//                        throw new IllegalArgumentException("Нельзя возобновлять прошедший заказ");
//                    }
                    List<Booking> getCrossedBookings = bookingRepository.getCrossedBookingsWithoutCurrent(existedBooking.getBkStartTime(), existedBooking.getBkEndTime(), existedBooking.getBox().getBoxId(), existedBooking.getBkId(),Arrays.asList(BookingStatus.BOOKED, BookingStatus.IN_PROGRESS, BookingStatus.DONE, BookingStatus.NOT_DONE));
                    if(!getCrossedBookings.isEmpty()){
                        throw new IllegalArgumentException("Заказ пересекается по времени с заказом " + getCrossedBookings.get(0).getBkId());
                    }
                    List<Booking> otherBookingsInSameTime = bookingRepository.notNegativeBookingsOfTransportInIntervalTime(existedBooking.getBkStartTime(), existedBooking.getBkEndTime(), existedBooking.getClientTransport().getClTrStateNumber(), existedBooking.getBkId(), List.of(BookingStatus.CANCELLED));
                    if(!otherBookingsInSameTime.isEmpty()){
                        Booking otherBookingInSameTime = otherBookingsInSameTime.get(0);
                        throw new IllegalArgumentException(String.format("Нельзя сформировать заказ, так как он пересекается по времени с заказом %s (бокс №%d, время начала %s, время окончания %s ), " +
                                "у которого указан транспорт с гос.номером %s и статус входит в следующий список: бронь, выполняется, выполнен, не выполнен",otherBookingInSameTime.getBkId(), otherBookingInSameTime.getBox().getBoxId(), otherBookingInSameTime.getBkStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), otherBookingInSameTime.getBkEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), otherBookingInSameTime.getClientTransport().getClTrStateNumber()));
                    }
                    existedBooking.setBkStatus(BookingStatus.BOOKED);

                }
                break;
            case NOT_DONE:
                if(existedBooking.getBkStatus() != BookingStatus.IN_PROGRESS) {
                    throw new IllegalArgumentException("Изменить статус на \"Не выполнен\" можно только заказу со статусом \"Выполняется\"");
                }else{
                    LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
                    if(now.isBefore(existedBooking.getBkEndTime())){
                        existedBooking.setBkEndTime(now);
                    }
                    existedBooking.setBkStatus(BookingStatus.NOT_DONE);
                }
                break;
            case DONE:
                if(existedBooking.getBkStatus() != BookingStatus.IN_PROGRESS) {
                    throw new IllegalArgumentException("Изменить статус на \"Выполнен\" можно только заказу со статусом \"Выполняется\"");
                }else{
                    LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
                    if(now.isBefore(existedBooking.getBkEndTime())){
                        existedBooking.setBkEndTime(now);
                    }
                    existedBooking.setBkStatus(BookingStatus.DONE);
                }
                break;
        }

        bookingRepository.save(existedBooking);

    }

    @Transactional
    public void delete(Booking existedBooking) {
        bookingRepository.delete(existedBooking);
    }

    @Transactional
    public void edit(Booking existedBooking, BookingDTO booking) {
        if(!booking.getBkStartTime().isBefore(booking.getBkEndTime())){
            throw new IllegalArgumentException("Время окончания выполнения заказа должно быть позже времени начала выполнения");
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
            throw new IllegalArgumentException(String.format("Нельзя изменить заказ, так как бокс %d закрыт", booking.getBox().getBoxId()));
        }
        existedBooking.setBkStartTime(booking.getBkStartTime());
        existedBooking.setBkEndTime(booking.getBkEndTime());
        existedBooking.setClientTransport(booking.getClientTransport());
        existedBooking.setBox(booking.getBox());
        existedBooking.setBkStatus(BookingStatus.BOOKED);

        List<Booking> getCrossedBookings = bookingRepository.getCrossedBookingsWithoutCurrent(existedBooking.getBkStartTime(), existedBooking.getBkEndTime(), existedBooking.getBox().getBoxId(), existedBooking.getBkId(),Arrays.asList(BookingStatus.BOOKED, BookingStatus.IN_PROGRESS, BookingStatus.DONE, BookingStatus.NOT_DONE));
        if(!getCrossedBookings.isEmpty()){
            throw new IllegalArgumentException("Заказ пересекается по времени с заказом " + getCrossedBookings.get(0).getBkId());
        }
        List<Booking> otherBookingsInSameTime = bookingRepository.notNegativeBookingsOfTransportInIntervalTime(existedBooking.getBkStartTime(), existedBooking.getBkEndTime(), existedBooking.getClientTransport().getClTrStateNumber(), existedBooking.getBkId(), List.of(BookingStatus.CANCELLED));
        if(!otherBookingsInSameTime.isEmpty()){
            Booking otherBookingInSameTime = otherBookingsInSameTime.get(0);
            throw new IllegalArgumentException(String.format("Нельзя изменить заказ, так как он пересекается по времени с заказом %s (бокс №%d, время начала %s, время окончания %s ), " +
                    "у которого указан транспорт с гос.номером %s и статус входит в следующий список: бронь, выполняется, выполнен, не выполнен",otherBookingInSameTime.getBkId(), otherBookingInSameTime.getBox().getBoxId(), otherBookingInSameTime.getBkStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), otherBookingInSameTime.getBkEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), otherBookingInSameTime.getClientTransport().getClTrStateNumber()));
        }
        int price = 0;
        existedBooking.setServices(new ArrayList<>());
        for (ServiceWithPriceListDTO serviceDTO: booking.getServices()){
            Optional<ru.pin120.carwashAPI.models.Service> serviceOptional = servService.getByServName(serviceDTO.getServName());
            if(serviceOptional.isEmpty()){
                throw new IllegalArgumentException("В базе данных отсутствует услуга " + serviceDTO.getServName());
            }else{
                ru.pin120.carwashAPI.models.Service service = serviceOptional.get();
                if(service.getCategoriesOfSupplies().isEmpty()){
                    throw new IllegalArgumentException(String.format("Услуге %s необходимо указать необходимую для выполнения категорию автомоечных средств", service.getServName()));
                }
                existedBooking.getServices().add(serviceOptional.get());
                price+=serviceDTO.getPlPrice();
            }
        }
        existedBooking.setBkPrice(calculatePrice(price, existedBooking.getClientTransport().getClient().getClDiscount()));
        bookingRepository.save(existedBooking);

    }
}
