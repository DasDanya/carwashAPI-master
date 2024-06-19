package ru.pin120.carwashAPI.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Класс с информацией о выполненных заказах мойщика
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InfoAboutWorkOfCleaner {
    /**
     * Рабочий день
     */
    private LocalDate day;
    /**
     * Количество выполненных заказов
     */
    private int countBookings;
    /**
     * Выручка мойщика с выполненных заказов
     */
    private int totalPrice;

}
