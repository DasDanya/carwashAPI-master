package ru.pin120.carwashAPI.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.pin120.carwashAPI.models.WorkSchedule;

import java.util.List;

/**
 * DTO с результатом установки рабочего графика мойщика
 */
@AllArgsConstructor
@Getter
@Setter
public class ResultCreateWorkSchedulesDTO {

    /**
     * Сообщение о конфликтах рабочих дней
     */
    private String conflictMessage;
    /**
     * Список рабочих дней
     */
    private List<WorkSchedule> createdWorkSchedules;
}
