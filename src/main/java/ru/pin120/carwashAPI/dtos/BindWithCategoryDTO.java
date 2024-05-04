package ru.pin120.carwashAPI.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BindWithCategoryDTO {
    @NotBlank(message = "Необходимо указать параметр")
    private String parameter;
    @NotBlank(message = "Необходимо указать категорию, для которой будет производиться привязка")
    private String catNameToBind;
}
