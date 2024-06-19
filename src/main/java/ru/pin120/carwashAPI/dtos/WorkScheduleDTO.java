package ru.pin120.carwashAPI.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.pin120.carwashAPI.models.WorkSchedule;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO рабочего дня
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkScheduleDTO {

    /**
     * id рабочего дня
     */
    @NotNull(message = "Необходимо указать id рабочего дня")
    private Long wsId;
    /**
     * День
     */
    @NotNull(message = "Необходимо указать рабочий день")
    private LocalDate wsWorkDay;

}
