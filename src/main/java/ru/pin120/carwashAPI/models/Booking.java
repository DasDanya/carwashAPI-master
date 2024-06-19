package ru.pin120.carwashAPI.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Модель заказа
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "bookings")
public class Booking {

    /**
     * Номер заказа
     */
    @Id
    @Column(length = 13)
    private String bkId;

    /**
     * Время начала выполнения
     */
    @NotNull(message = "Необходимо указать время начала выполнения заказа")
    private LocalDateTime bkStartTime;

    /**
     * Время окончания выполнения
     */
    @NotNull(message = "Необходимо указать время окончания выполнения заказа")
    private LocalDateTime bkEndTime;

    /**
     * Стоимость
     */
    @Min(value = 0, message = "Стоимость заказа не может быть отрицательной")
    @NotNull(message = "Необходимо указать стоимость заказа")
    private Integer bkPrice;

    /**
     * Статус
     */
    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Необходимо указать статус")
    private BookingStatus bkStatus;

    /**
     * Бокс
     */
    @ManyToOne
    @JoinColumn(name = "box_id", nullable = false)
    @NotNull(message = "Необходимо указать бокс")
    private Box box;

    /**
     * Мойщик
     */
    @ManyToOne
    @JoinColumn(name = "clr_id")
    private Cleaner cleaner;

    /**
     * Транспорт клиента
     */
    @ManyToOne
    @JoinColumn(name = "cl_tr_id", nullable = false)
    @NotNull(message = "Необходимо указать транспорт клиента")
    private ClientsTransport clientTransport;

    /**
     * Список услуг
     */
    @ManyToMany
    @JoinTable(name = "services_in_booking",
            joinColumns = @JoinColumn(name = "bk_id",nullable = false),
            inverseJoinColumns = @JoinColumn(name = "serv_name",nullable = false),
            uniqueConstraints = @UniqueConstraint(columnNames = {"bk_id", "serv_name"}))
    private List<Service> services;

}
