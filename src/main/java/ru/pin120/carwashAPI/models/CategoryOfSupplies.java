package ru.pin120.carwashAPI.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories_of_supplies")
public class CategoryOfSupplies {

    @Id
    @Column(unique = true,nullable = false, length = 30)
    @Size(max = 30, message = "Максимальная длина 30 символов")
    @Pattern(regexp = "^[а-яА-ЯёЁ -]+$", message = "Допустимые символы для названия: кириллица и пробелы")
    @NotBlank(message = "Необходимо ввести название категории автомоечных средств")
    private String cSupName;


}
