package ru.pin120.carwashAPI.dtos;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.pin120.carwashAPI.models.BookingStatus;
import ru.pin120.carwashAPI.models.Box;
import ru.pin120.carwashAPI.models.Cleaner;
import ru.pin120.carwashAPI.models.ClientsTransport;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingDTO {

    private String bkId;

    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Необходимо указать статус")
    private BookingStatus bkStatus;

    @NotNull(message = "Необходимо указать время начала выполнения заказа")
    private LocalDateTime bkStartTime;

    @NotNull(message = "Необходимо указать время окончания выполнения заказа")
    private LocalDateTime bkEndTime;

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

    @NotEmpty(message = "Необходимо указать услугу")
    @Valid
    private List<ServiceWithPriceListDTO> services;

}
