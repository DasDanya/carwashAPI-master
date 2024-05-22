package ru.pin120.carwashAPI.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.pin120.carwashAPI.models.Box;
import ru.pin120.carwashAPI.models.CleanerStatus;
import ru.pin120.carwashAPI.models.WorkSchedule;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CleanerDTO {
    private Long clrId;
    private String clrSurname;
    private String clrName;
    private String clrPatronymic;
    private CleanerStatus clrStatus;
    private Box box;
    private List<WorkSchedule> workSchedules;

}
