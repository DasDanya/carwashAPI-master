package ru.pin120.carwashAPI.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Модель рабочего дня мойщика
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="work_schedule", uniqueConstraints = @UniqueConstraint(columnNames = {"ws_work_day","clr_id","box_id"}))

public class WorkSchedule {

    /**
     * id рабочего дня
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wsId;

    /**
     * День
     */
    @NotNull(message = "Необходимо указать рабочий день")
    private LocalDate wsWorkDay;

    /**
     * Мойщик
     */
    @ManyToOne
    @JoinColumn(name = "clr_id", nullable = false)
    @NotNull(message = "Необходимо указать мойщика")
    private Cleaner cleaner;

    /**
     * Бокс
     */
    @ManyToOne
    @JoinColumn(name = "box_id", nullable = false)
    @NotNull(message = "Необходимо указать бокс")
    private Box box;

}
