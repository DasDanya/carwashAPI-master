package ru.pin120.carwashAPI.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.pin120.carwashAPI.models.WorkSchedule;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ResultCreateWorkSchedulesDTO {

    private String conflictMessage;
    private List<WorkSchedule> createdWorkSchedules;
}
