package ru.pin120.carwashAPI.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * DTO услуги вместе с её стоимостью и временем выполнения
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ServiceWithPriceListDTO {

    /**
     * Название категории услуг
     */
    @Size(max = 30, message = "Максимальная длина 30 символов")
    @NotBlank(message = "Необходимо ввести название категории услуг")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9 -]+$", message = "Допустимые символы для названия: латинские буквы, кириллица, цифры, пробелы и знаки тире")
    private String catName;

    /**
     * Название услуги
     */
    @Size(max = 30, message = "Максимальная длина 30 символов")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9 -]+$", message = "Допустимые символы для названия: латинские буквы, кириллица, цифры, пробелы и знаки тире")
    @NotBlank(message = "Необходимо ввести название услуги")
    private String servName;

    /**
     * Стоимость выполнения
     */
    @Min(value = 1, message = "Минимальная стоимость = 1 ₽")
    @Max(value = 50000, message = "Минимальная стоимость = 50000 ₽")
    private Integer plPrice;

    /**
     * Время выполнения
     */
    @Min(value = 1, message = "Минимальное время выполнения - 1 минута")
    @Max(value = 1440, message = "Максимальное время выполнения - 1 день")
    private Integer plTime;


}
