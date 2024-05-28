package ru.pin120.carwashAPI.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "supplies", uniqueConstraints = @UniqueConstraint(columnNames = {"sup_name","sup_cat","sup_measure"}))
public class Supply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supId;

    @Column(nullable = false, length = 50)
    @Size(max = 50, message = "Максимальная длина названия = 50 символов")
    @NotBlank(message = "Необходимо ввести имя сотрудника")
    private String supName;

    @Min(value = 0, message = "Количество средства должно быть неотрицательным")
    private int supCount;

    @Min(value = 1, message = "Минимальный(-ое) объём/количество единицы средства = 1")
    private int supMeasure;

    @Column(length = 20)
    private String supPhotoName;

    @ManyToOne
    @JoinColumn(name = "sup_cat", nullable = false)
    @NotNull(message = "Необходимо указать категорию")
    private CategoryOfSupplies category;

}
