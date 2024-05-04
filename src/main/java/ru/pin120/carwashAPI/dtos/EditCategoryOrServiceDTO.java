package ru.pin120.carwashAPI.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditCategoryOrServiceDTO {

    @NotBlank(message = "Необходимо ввести старое название")
    @Size(max = 30, message = "Максимальноe допустимое количество символов для старого названия = 30")
    private String pastName;

    @NotBlank(message = "Необходимо ввести новое название")
    @Size(max = 30, message = "Максимальноe допустимое количество символов для нового названия = 30")
    private String newName;
}
