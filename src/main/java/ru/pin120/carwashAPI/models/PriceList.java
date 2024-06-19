package ru.pin120.carwashAPI.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Модель позиции в прайс-листе
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "price_list",uniqueConstraints = @UniqueConstraint(columnNames = {"pl_serv_name", "pl_cat_tr_id"}))
public class PriceList {

    /**
     * id позиции
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long plId;


    /**
     * Стоимость выполнения
     */
    @NotNull(message = "Необходимо указать цену")
    @PositiveOrZero(message = "Цена не может быть отрицательной")
    @Min(value = 1, message = "Минимальная стоимость = 1 ₽")
    @Max(value = 50000, message = "Минимальная стоимость = 50000 ₽")
    private Integer plPrice;

    /**
     * Время выполнения
     */
    @NotNull(message = "Необходимо указать время выполнения")
    @Min(value = 1, message = "Минимальное время выполнения - 1 минута")
    @Max(value = 1440, message = "Максимальное время выполнения - 1 день")
    private Integer plTime;

    /**
     * Услуга
     */
    @ManyToOne
    @NotNull(message = "Необходимо указать услугу")
    @JoinColumn(name = "pl_serv_name",nullable = false)
    private Service service;

    /**
     * Категория транспорта
     */
    @ManyToOne
    @NotNull(message = "Необходимо указать категорию автомобилей")
    @JoinColumn(name = "pl_cat_tr_id",nullable = false)
    private CategoryOfTransport categoryOfTransport;


}
