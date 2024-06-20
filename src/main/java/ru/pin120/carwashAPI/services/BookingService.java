package ru.pin120.carwashAPI.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pin120.carwashAPI.dtos.BookingDTO;
import ru.pin120.carwashAPI.dtos.BookingsInfoDTO;
import ru.pin120.carwashAPI.dtos.ServiceWithPriceListDTO;
import ru.pin120.carwashAPI.models.*;
import ru.pin120.carwashAPI.repositories.BookingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис заказа
 */
@Service
public class BookingService {

    /**
     * Репозиторий заказа
     */
    private final BookingRepository bookingRepository;
    /**
     * Сервис последовательности генерации номера заказа
     */
    private final BookingIdSequenceService bookingIdSequenceService;
    /**
     * Сервис услуг
     */
    private final ServService servService;
    /**
     * Сервис расходных материалов в боксе
     */
    private final SuppliesInBoxService suppliesInBoxService;
    /**
     * Сервис позиций в прайс-листе
     */
    private final PriceListService priceListService;

    @Autowired
    private Environment environment;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Внедрение зависимостей
     * @param bookingRepository репозиторий заказа
     * @param bookingIdSequenceService сервис последовательности генерации номера заказа
     * @param servService сервис услуг
     * @param suppliesInBoxService сервис расходных материалов в боксе
     * @param priceListService сервис позиций в прайс-листе
     */
    public BookingService(BookingRepository bookingRepository, BookingIdSequenceService bookingIdSequenceService, ServService servService, SuppliesInBoxService suppliesInBoxService, PriceListService priceListService) {
        this.bookingRepository = bookingRepository;
        this.bookingIdSequenceService = bookingIdSequenceService;
        this.servService = servService;
        this.suppliesInBoxService = suppliesInBoxService;
        this.priceListService = priceListService;
    }


    /**
     * Получение заказов бокса
     * @param startInterval начало временного интервала
     * @param endInterval конец временного интервала
     * @param boxId id бокса
     * @return Список заказов
     */
    public List<Booking> getBoxBookings(LocalDateTime startInterval, LocalDateTime endInterval, Long boxId){
        return bookingRepository.getBoxBookings(startInterval, endInterval, boxId);
    }

    /**
     * Создание заказа
     * @param booking данные о заказе
     * @return Созданный заказ
     */
    @Transactional
    public Booking create(@Valid BookingDTO booking) {
        LocalTime startWorkTime = LocalTime.parse(environment.getProperty("START_WORK_TIME"));
        LocalTime endWorkTime = LocalTime.parse(environment.getProperty("END_WORK_TIME"));

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
        if(bkStartTime.isBefore(LocalTime.parse(environment.getProperty("START_WORK_TIME")))){
            throw new IllegalArgumentException("Время начала заказа не может быть раньше " + startWorkTime);
        }
        if(bkEndTime.isBefore(startWorkTime)){
            throw new IllegalArgumentException("Время окончания заказа не может быть раньше " + startWorkTime);
        }
        if(!bkStartTime.isBefore(endWorkTime)){
            throw new IllegalArgumentException("Время начала заказа не может быть позднее " + endWorkTime.minusMinutes(1));
        }
        if(bkEndTime.isAfter(endWorkTime)){
            throw new IllegalArgumentException("Время окончания заказа не может быть позднее " + endWorkTime);
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
//        } есть проверка в сущности
        createdBooking.setServices(new ArrayList<>());
        for (ServiceWithPriceListDTO serviceDTO: booking.getServices()){
            Optional<ru.pin120.carwashAPI.models.Service> serviceOptional = servService.getByServName(serviceDTO.getServName());
            if(serviceOptional.isEmpty()){
                throw new IllegalArgumentException("В базе данных отсутствует услуга " + serviceDTO.getServName());
            }else{
//                ru.pin120.carwashAPI.models.Service service = serviceOptional.get();
//                if(service.getCategoriesOfSupplies().isEmpty()){
//                    throw new IllegalArgumentException(String.format("Услуге %s необходимо указать необходимую для выполнения категорию автомоечных средств", service.getServName()));
//                }
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

        return bookingRepository.getCrossedBookings(startInterval, endInterval, boxId, Arrays.asList(BookingStatus.BOOKED, BookingStatus.IN_PROGRESS, BookingStatus.DONE, BookingStatus.NOT_DONE));
    }

    /**
     * Расчёт стоимости заказа с учётом скидки клиента
     * @param price стоимость без учёта скидки
     * @param discount скидка
     * @return Стоимость с учётом скидки
     */
    public int calculatePrice(int price, int discount){
        double discountedPrice = price * (1 - (discount / 100.0));
        return (int) Math.floor(discountedPrice);
    }

    /**
     * Получение заказа по id
     * @param bkId id заказа
     * @return Объект Optional с заказом, если он существует
     */
    public Optional<Booking> getByBkId(String bkId){
        return bookingRepository.findById(bkId);
    }

    /**
     * Смена статуса заказа
     * @param bookingDTO новые данные о заказе
     * @param existedBooking заказ, у которого сменяется статус
     */
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
                    }else{
                        existedBooking.setCleaner(workSchedule.getCleaner());
                    }

                    CategoryOfTransport categoryOfTransport = existedBooking.getClientTransport().getTransport().getCategoryOfTransport();
                    for(ru.pin120.carwashAPI.models.Service service: existedBooking.getServices()){
                        if(!priceListService.existPriceListPosition(categoryOfTransport.getCatTrId(), service.getServName())){
                            throw new IllegalArgumentException(String.format("Нельзя начать выполнение заказа, так как в настоящее время для категории транспорта %s отсутствует возможность выполнения услуги %s", categoryOfTransport.getCatTrName(), service.getServName()));
                        }
                        List<CategoryOfSupplies> categoriesOfSupplies = service.getCategoriesOfSupplies();
                        for (CategoryOfSupplies category : categoriesOfSupplies) {
                            List<SuppliesInBox> suppliesInBox = suppliesInBoxService.getListExistingSuppliesCertainCategory(existedBooking.getBox().getBoxId(), category.getCSupName());
                            if (suppliesInBox.isEmpty()) {
                                throw new IllegalArgumentException("В боксе отсутствует расходный материал категории " + category.getCSupName());
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

    /**
     * Удаление заказа
     * @param existedBooking заказ
     */
    @Transactional
    public void delete(Booking existedBooking) {
        bookingRepository.delete(existedBooking);
    }

    /**
     * Изменение данных о заказе
     * @param existedBooking изменяемый заказ
     * @param booking новые данные о заказе
     */
    @Transactional
    public void edit(Booking existedBooking, BookingDTO booking) {
        LocalTime startWorkTime = LocalTime.parse(environment.getProperty("START_WORK_TIME"));
        LocalTime endWorkTime = LocalTime.parse(environment.getProperty("END_WORK_TIME"));

        if(!booking.getBkStartTime().isBefore(booking.getBkEndTime())){
            throw new IllegalArgumentException("Время окончания выполнения заказа должно быть позже времени начала выполнения");
        }
        LocalTime bkEndTime = LocalTime.of(booking.getBkEndTime().getHour(), booking.getBkEndTime().getMinute());
        LocalTime bkStartTime = LocalTime.of(booking.getBkStartTime().getHour(), booking.getBkStartTime().getMinute());
        if(bkStartTime.isBefore(startWorkTime)){
            throw new IllegalArgumentException("Время начала заказа не может быть раньше " + startWorkTime);
        }
        if(bkEndTime.isBefore(startWorkTime)){
            throw new IllegalArgumentException("Время окончания заказа не может быть раньше " + startWorkTime);
        }
        if(!bkStartTime.isBefore(endWorkTime)){
            throw new IllegalArgumentException("Время начала заказа не может быть позднее " + endWorkTime.minusMinutes(1));
        }
        if(bkEndTime.isAfter(endWorkTime)){
            throw new IllegalArgumentException("Время окончания заказа не может быть позднее " + endWorkTime);
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
//                ru.pin120.carwashAPI.models.Service service = serviceOptional.get();
//                if(service.getCategoriesOfSupplies().isEmpty()){
//                    throw new IllegalArgumentException(String.format("Услуге %s необходимо указать необходимую для выполнения категорию автомоечных средств", service.getServName()));
//                }
                existedBooking.getServices().add(serviceOptional.get());
                price+=serviceDTO.getPlPrice();
            }
        }
        existedBooking.setBkPrice(calculatePrice(price, existedBooking.getClientTransport().getClient().getClDiscount()));
        bookingRepository.save(existedBooking);

    }

    /**
     * Создание запроса на получение заказов
     * @param cleanerId id мойщика
     * @param clientId id клиента
     * @param boxId id бокса
     * @param startInterval начало временного интервала
     * @param endInterval конец временного интервала
     * @param bookingStatus статус
     * @param compareOperator оператор сравнения стоимости
     * @param price стоимость
     * @return Запрос на получение заказов
     */
    private TypedQuery<Booking> createQuery(Long cleanerId, Long clientId, Long boxId, LocalDateTime startInterval, LocalDateTime endInterval, BookingStatus bookingStatus, String compareOperator, Integer price){
        Map<String, Object> parameters = new HashMap<>();
        String baseQuery = "SELECT b FROM Booking b ";
        String partQuery = "";
        if(cleanerId != null){
            partQuery = " b.cleaner.clrId = :cleanerId ";
            parameters.put("cleanerId", cleanerId);
        }
        if(clientId != null){
            if(partQuery.isBlank()){
                partQuery = " b.clientTransport.client.clId = :clientId ";
            }else{
                partQuery += " AND b.clientTransport.client.clId = :clientId ";
            }
            parameters.put("clientId", clientId);
        }
        if(boxId != null){
            if(partQuery.isBlank()){
                partQuery = " b.box.boxId = :boxId ";
            }else{
                partQuery += " AND b.box.boxId = :boxId ";
            }
            parameters.put("boxId", boxId);
        }
        if(startInterval != null){
            if(partQuery.isBlank()){
                partQuery = " b.bkStartTime >= :startInterval ";
            }else {
                partQuery += " AND b.bkStartTime >= :startInterval ";
            }
            parameters.put("startInterval", startInterval);
        }
        if(endInterval != null){
            if(partQuery.isBlank()) {
                partQuery = " b.bkStartTime <= :endInterval ";
            }else{
                partQuery += " AND b.bkStartTime <= :endInterval ";
            }
            parameters.put("endInterval", endInterval);
        }
        if(bookingStatus != null){
            if(partQuery.isBlank()) {
                partQuery = " b.bkStatus = :status ";
            }else {
                partQuery += " AND b.bkStatus = :status ";
            }
            parameters.put("status", bookingStatus);
        }
        if(compareOperator != null && price != null){
            if(partQuery.isBlank()) {
                partQuery = " b.bkPrice " + compareOperator + " :price";
            }else{
                partQuery += " AND b.bkPrice " + compareOperator + " :price";
            }
            parameters.put("price", price);
        }
        if(!partQuery.isBlank()) {
            partQuery += " ORDER BY b.bkEndTime DESC";
            baseQuery = baseQuery + " WHERE " + partQuery;
        }else{
            baseQuery += " ORDER BY b.bkEndTime DESC";
        }


        TypedQuery<Booking> query = entityManager.createQuery(baseQuery, Booking.class);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query;
    }

    /**
     * Получение списка заказов клиента
     * @param pageIndex индекс страницы
     * @param cleanerId id мойщика
     * @param clientId id клиента
     * @param boxId id бокса
     * @param startInterval начало временного интервала
     * @param endInterval конец временного интервала
     * @param bookingStatus статус
     * @param compareOperator оператор сравнения стоимости
     * @param price стоимость
     * @return Список заказов
     */
    public List<Booking> getClientBookings(Integer pageIndex, Long cleanerId, Long clientId, Long boxId, LocalDateTime startInterval, LocalDateTime endInterval, BookingStatus bookingStatus, String compareOperator, Integer price) {
        int countItemsInPage = Integer.parseInt(environment.getProperty("COUNT_ITEMS_IN_PAGE"));
        Pageable pageable = PageRequest.of(pageIndex, countItemsInPage);
        TypedQuery<Booking> query = createQuery(cleanerId, clientId, boxId, startInterval, endInterval, bookingStatus, compareOperator, price);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return query.getResultList();

    }

    /**
     * Получение информации о количестве и стоимости заказов
     * @param cleanerId id мойщика
     * @param clientId id клиента
     * @param boxId id бокса
     * @param startInterval начало временного интервала
     * @param endInterval конец временного интервала
     * @param bookingStatus статус
     * @param compareOperator оператор сравнения стоимости
     * @param price стоимость
     * @return Информация о количестве и стоимости заказов
     */
    public BookingsInfoDTO getBookingsInfo(Long cleanerId, Long clientId, Long boxId, LocalDateTime startInterval, LocalDateTime endInterval, BookingStatus bookingStatus, String compareOperator, Integer price){
        TypedQuery<Booking> query = createQuery(cleanerId, clientId, boxId, startInterval, endInterval, bookingStatus, compareOperator, price);
        List<Booking> bookings = query.getResultList();

        int totalPrice = bookings.stream()
                .mapToInt(Booking::getBkPrice)
                .sum();

        return new BookingsInfoDTO(bookings.size(), totalPrice);
    }

    /**
     * Получение данных о выполненных заказах мойщика
     * @param cleanerId id мойщика
     * @param startInterval начало временного интервала
     * @param endInterval конец временного интервала
     * @return Map c рабочими днями и данными о выполненных заказах
     */
    public Map<LocalDate, BookingsInfoDTO> infoAboutWorkOfCleaner(Long cleanerId, LocalDateTime startInterval, LocalDateTime endInterval){
        Map<String, Object> parameters = new HashMap<>();
        String baseQuery = "SELECT b FROM Booking b WHERE b.cleaner.clrId = :cleanerId AND b.bkStatus = :bkStatus ";
        parameters.put("cleanerId", cleanerId);
        parameters.put("bkStatus", BookingStatus.DONE);
        if(startInterval != null){
            baseQuery += " AND b.bkStartTime >= :startInterval ";
            parameters.put("startInterval", startInterval);
        }
        if(endInterval != null){
            baseQuery += " AND b.bkStartTime <= :endInterval ";
            parameters.put("endInterval", endInterval);
        }


        TypedQuery<Booking> query = entityManager.createQuery(baseQuery, Booking.class);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        List<Booking> bookings = query.getResultList();

        return bookings.stream()
               .collect(Collectors.groupingBy(
                       booking -> booking.getBkStartTime().toLocalDate(),
                       Collectors.collectingAndThen(Collectors.toList(), this::getSummary)
               ))
               .entrySet().stream()
               .sorted(Map.Entry.<LocalDate, BookingsInfoDTO>comparingByKey().reversed()) // Сортировка по ключу в обратном порядке
               .collect(Collectors.toMap(
                       Map.Entry::getKey,
                       Map.Entry::getValue,
                       (e1, e2) -> e1,
                       LinkedHashMap::new
               ));
    }

    /**
     * Получение данных о работе мойщика
     * @param bookings список заказов мойщика
     * @return Информация о работе мойщика
     */
    private BookingsInfoDTO getSummary(List<Booking> bookings) {
        double cleanerStake = Double.valueOf(environment.getProperty("CLEANER_STAKE"));
        int count = bookings.size();
        int totalCost = (int) Math.ceil(bookings.stream()
                .mapToInt(Booking::getBkPrice)
                .sum() * cleanerStake);
        return new BookingsInfoDTO(count, totalCost);
    }
}
