package ru.pin120.carwashAPI.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Модель для генерации номера заказа
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingIdSequence {
    /**
     * Год
     */
    @Id
    private int year;
    /**
     * Крайний номер заказа
     */
    @Min(value = 1, message = "Минимальное значение id = 1")
    private int lastId;
}
