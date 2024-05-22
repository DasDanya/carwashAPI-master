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

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="work_schedule", uniqueConstraints = @UniqueConstraint(columnNames = {"ws_work_day","clr_id"}))
public class WorkSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wsId;

    @NotNull(message = "Необходимо указать рабочий день")
    private LocalDate wsWorkDay;

    @ManyToOne
    @JoinColumn(name = "clr_id", nullable = false)
    @NotNull(message = "Необходимо указать мойщика")
    @JsonIgnore
    private Cleaner cleaner;
}
