package ru.pin120.carwashAPI.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO с данными об общем количестве и стоимости выполнения заказов
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingsInfoDTO {

    /**
     * Количество заказов
     */
    private int totalCount;
    /**
     * Стоимость заказов
     */
    private int totalPrice;
}
