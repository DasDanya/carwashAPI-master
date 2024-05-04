package ru.pin120.carwashAPI.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "price_list",uniqueConstraints = @UniqueConstraint(columnNames = {"pl_serv_name", "pl_cat_tr_id"}))
public class PriceList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long plId;


    @NotNull(message = "Необходимо указать цену")
    @PositiveOrZero(message = "Цена не может быть отрицательной")
    private Integer plPrice;

    @NotNull(message = "Необходимо указать время выполнения")
    @Min(value = 1, message = "Минимальное время выполнения - 1 минута")
    private Integer plTime;


    @ManyToOne
    @NotNull(message = "Необходимо указать услугу")
    @JoinColumn(name = "pl_serv_name",nullable = false)
    private Service service;

    @ManyToOne
    @NotNull(message = "Необходимо указать категорию автомобилей")
    @JoinColumn(name = "pl_cat_tr_id",nullable = false)
    private CategoryOfTransport categoryOfTransport;


}
