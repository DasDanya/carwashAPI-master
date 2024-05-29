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

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "supplies_in_box", uniqueConstraints = @UniqueConstraint(columnNames = {"box_id","sup_id"}))
public class SuppliesInBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sibId;


    @Min(value = 0, message = "Количество автомоечного средства в боксе должно быть неотрицательным")
    private int countSupplies;

    @ManyToOne
    @JoinColumn(name = "box_id", nullable = false)
    @NotNull(message = "Необходимо указать бокс")
    private Box box;

    @ManyToOne
    @JoinColumn(name = "sup_id", nullable = false)
    @NotNull(message = "Необходимо указать автомоечное средство")
    private Supply supply;

}
