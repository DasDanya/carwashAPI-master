package ru.pin120.carwashAPI.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Модель расходного материала
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "supplies", uniqueConstraints = @UniqueConstraint(columnNames = {"sup_name","sup_cat","sup_measure"}))

public class Supply {

    /**
     * id расходного материала
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supId;

    /**
     * Название
     */
    @Column(nullable = false, length = 50)
    @Size(max = 50, message = "Максимальная длина названия = 50 символов")
    @NotBlank(message = "Необходимо ввести название расходного материала")
    private String supName;

    /**
     * Количество
     */
    @Min(value = 0, message = "Количество расходного материала должно быть неотрицательным")
    private int supCount;

    /**
     * Количество единицы
     */
    @Min(value = 1, message = "Минимальный(-ое) объём/количество единицы средства = 1")
    private int supMeasure;

    /**
     * Название фотографии
     */
    @Column(length = 20)
    private String supPhotoName;

    /**
     * Категория
     */
    @ManyToOne
    @JoinColumn(name = "sup_cat", nullable = false)
    @NotNull(message = "Необходимо указать категорию")
    private CategoryOfSupplies category;

    /**
     * Расходные материалы в боксе
     */
    @OneToMany(mappedBy = "supply", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SuppliesInBox> suppliesInBoxes;

}
