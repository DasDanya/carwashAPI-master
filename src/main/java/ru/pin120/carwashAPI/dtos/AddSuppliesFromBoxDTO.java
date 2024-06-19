package ru.pin120.carwashAPI.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.pin120.carwashAPI.models.SuppliesInBox;

/**
 * DTO для добавления расходных материалов
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddSuppliesFromBoxDTO {
    /**
     * Расходный материал в боксе
     */
    @Valid
    private SuppliesInBox suppliesInBox;

    /**
     * Количество добавляемого расходного материала
     */
    @PositiveOrZero(message = "Количество должно быть неотрицательным")
    private int countOfAdded;
}
