package ru.pin120.carwashAPI.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO с категориями и услугами автомойки
 */
@Getter
@Setter
@NoArgsConstructor
public class CategoriesWithServicesDTO {

    /**
     * Название категории
     */
    private String categoryName;
    /**
     * Услуги категории
     */
    private List<String> servicesOfCategory;
}
