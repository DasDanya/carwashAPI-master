package ru.pin120.carwashAPI.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Модель бокса
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "boxes")
public class Box {

    /**
     * Номер бокса
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boxId;

    /**
     * Статус
     */
    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Необходимо указать статус")
    private BoxStatus boxStatus;

    /**
     * Список рабочих дней
     */
    @OneToMany(mappedBy = "box", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<WorkSchedule> workSchedules;

    /**
     * Список расходных материалов в боксе
     */
    @OneToMany(mappedBy = "box", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SuppliesInBox> suppliesInBoxes;

    /**
     * Список заказов
     */
    @OneToMany(mappedBy = "box")
    @JsonIgnore
    private List<Booking> bookings;


}
