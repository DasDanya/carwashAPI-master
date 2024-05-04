package ru.pin120.carwashAPI.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ServiceDTO {

    @NotBlank(message = "Необходимо ввести название услуги")
    @Size(max = 30, message = "Максимальноe допустимое количество символов для названия услуги = 30")
    private String servName;

    @NotBlank(message = "Необходимо ввести название категории")
    @Size(max = 30, message = "Максимальное допустимое количество символов для названия категории = 30")
    private String catName;
}
