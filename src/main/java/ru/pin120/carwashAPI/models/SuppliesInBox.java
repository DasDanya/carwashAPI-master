package ru.pin120.carwashAPI.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Модель расходных материалов в боксе
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "supplies_in_box", uniqueConstraints = @UniqueConstraint(columnNames = {"box_id","sup_id"}))
public class SuppliesInBox {
    /**
     * id расходных материалов в боксе
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sibId;

    /**
     * Количество расходных материалов
     */
    @Min(value = 0, message = "Количество расходного материала в боксе должно быть неотрицательным")
    private int countSupplies;

    /**
     * Бокс
     */
    @ManyToOne
    @JoinColumn(name = "box_id", nullable = false)
    @NotNull(message = "Необходимо указать бокс")
    private Box box;

    /**
     * Расходный материал
     */
    @ManyToOne
    @JoinColumn(name = "sup_id", nullable = false)
    @NotNull(message = "Необходимо указать расходный материал")
    private Supply supply;

}
