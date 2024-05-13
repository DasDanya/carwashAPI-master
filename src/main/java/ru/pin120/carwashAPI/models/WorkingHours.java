package ru.pin120.carwashAPI.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "working_hours")
public class WorkingHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long whId;

    @NotNull(message = "Необходимо указать время начала")
    private LocalDateTime whStartTime;

    @NotNull(message = "Необходимо указать время окончания")
    private LocalDateTime whEndTime;

    // Подумать над статусом (работает, болен, в отпуске)

    @ManyToOne
    @JoinColumn(name = "clr_id", nullable = false)
    @NotNull(message = "Необходимо указать мойщика")
    private Cleaner cleaner;
}
