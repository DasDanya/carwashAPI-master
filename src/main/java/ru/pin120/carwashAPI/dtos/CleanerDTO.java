package ru.pin120.carwashAPI.dtos;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @Size(max = 50, message = "Максимальная длина фамилии = 50 символов")
    @Pattern(regexp = "^[А-ЯЁа-яё-]+$", message = "Некорректный ввод фамилии")
    @NotBlank(message = "Необходимо ввести фамилию сотрудника")
    private String clrSurname;

    @Size(max = 50, message = "Максимальная длина имени = 50 символов")
    @Pattern(regexp = "^[А-ЯЁа-яё-]+$", message = "Некорректный ввод имени")
    @NotBlank(message = "Необходимо ввести имя сотрудника")
    private String clrName;

    @Size(max = 50, message = "Максимальная длина имени = 50 символов")
    @Pattern(regexp = "^(|[А-ЯЁа-яё-]+)$", message = "Некорректный ввод отчества")
    private String clrPatronymic;

    @NotNull(message = "Необходимо указать статус")
    private CleanerStatus clrStatus;

    @NotNull(message = "Необходимо указать бокс")
    private Box box;

    private List<WorkSchedule> workSchedules;

}
