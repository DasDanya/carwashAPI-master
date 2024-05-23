package ru.pin120.carwashAPI.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.pin120.carwashAPI.models.WorkSchedule;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkScheduleDTO {

    @NotNull(message = "Необходимо указать id мойщика")
    private Long clrId;
    @Valid
    private List<WorkSchedule> workSchedules;
}