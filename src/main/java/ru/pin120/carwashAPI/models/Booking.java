package ru.pin120.carwashAPI.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "bookings")
public class Booking {

    @Id
    @Column(length = 13)
    private String bkId;

    @NotNull(message = "Необходимо указать время начала выполнения заказа")
    private LocalDateTime bkStartTime;

    @NotNull(message = "Необходимо указать время окончания выполнения заказа")
    private LocalDateTime bkEndTime;

    @Min(value = 0, message = "Стоимость заказа не может быть отрицательной")
    @NotNull(message = "Необходимо указать стоимость заказа")
    private Integer bkPrice;

    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Необходимо указать статус")
    private BookingStatus bkStatus;

    @ManyToOne
    @JoinColumn(name = "box_id", nullable = false)
    @NotNull(message = "Необходимо указать бокс")
    private Box box;

    @ManyToOne
    @JoinColumn(name = "clr_id")
    private Cleaner cleaner;

    @ManyToOne
    @JoinColumn(name = "cl_tr_id", nullable = false)
    @NotNull(message = "Необходимо указать транспорт клиента")
    private ClientsTransport clientTransport;

    @ManyToMany
    @JoinTable(name = "services_in_booking",
            joinColumns = @JoinColumn(name = "bk_id",nullable = false),
            inverseJoinColumns = @JoinColumn(name = "serv_name",nullable = false),
            uniqueConstraints = @UniqueConstraint(columnNames = {"bk_id", "serv_name"}))
    private List<Service> services;

}
