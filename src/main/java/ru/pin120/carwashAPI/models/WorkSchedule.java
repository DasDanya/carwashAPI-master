package ru.pin120.carwashAPI.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="work_schedule")
public class WorkSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wsId;

    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Необходимо указать день начала смены")
    private Days wsStartDay;

    @NotNull(message = "Необходимо указать время начала смены")
    private LocalTime wsStartTime;

    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Необходимо указать день окончания смены")
    private Days wsEndDay;

    @NotNull(message = "Необходимо указать время окончания смены")
    private LocalTime wsEndTime;

    @ManyToOne
    @JoinColumn(name = "clr_id", nullable = false)
    @NotNull(message = "Необходимо указать мойщика")
    @JsonIgnore
    private Cleaner cleaner;
}
