package ru.pin120.carwashAPI.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO для связи услуг/услуги автомойки с категорией
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BindWithCategoryDTO {
    /**
     * Параметр связи (категория или услуга)
     */
    @NotBlank(message = "Необходимо указать параметр")
    private String parameter;
    /**
     * Название категории, к которой происходит привязка
     */
    @NotBlank(message = "Необходимо указать категорию, для которой будет производиться привязка")
    private String catNameToBind;
}
