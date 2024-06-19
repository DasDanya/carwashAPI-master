package ru.pin120.carwashAPI.models;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Модель категории услуг
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "categories_of_services")
public class CategoryOfServices {

    /**
     * Название
     */
    @Id
    @Column(unique = true, nullable = false, length = 30)
    @Size(max = 30, message = "Максимальная длина 30 символов")
    @NotBlank(message = "Необходимо ввести название категории услуг")
    //@Pattern(regexp = "^[А-Яа-яЁё\\s\\-]+$", message = "Название должно состоять из русских букв, пробелов и знака тире")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9 -]+$", message = "Допустимые символы для названия: латинские буквы, кириллица, цифры, пробелы и знаки тире")
    private String catName;

    /**
     * Список услуг
     */
    @OneToMany(mappedBy = "category")
    @JsonManagedReference
    private List<Service> services;
}
