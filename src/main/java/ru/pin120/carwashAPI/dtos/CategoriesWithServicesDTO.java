package ru.pin120.carwashAPI.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CategoriesWithServicesDTO {

    private String categoryName;
    private List<String> servicesOfCategory;
}
