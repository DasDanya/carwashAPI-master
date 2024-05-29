package ru.pin120.carwashAPI.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.pin120.carwashAPI.models.SuppliesInBox;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddSuppliesFromBoxDTO {
    @Valid
    private SuppliesInBox suppliesInBox;

    @PositiveOrZero(message = "Количество должно быть неотрицательным")
    private int countOfAdded;
}
