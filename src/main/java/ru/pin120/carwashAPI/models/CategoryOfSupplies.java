package ru.pin120.carwashAPI.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories_of_supplies")
public class CategoryOfSupplies {

    @Id
    @Column(unique = true,nullable = false, length = 30)
    @Size(max = 30, message = "Максимальная длина 30 символов")
    @Pattern(regexp = "^[а-яА-ЯёЁ -]+$", message = "Допустимые символы для названия: кириллица и пробелы")
    @NotBlank(message = "Необходимо ввести название категории автомоечных средств")
    private String cSupName;

    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Необходимо указать меру измерения")
    private UnitOfMeasure unit;

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Supply> supplies;

}
