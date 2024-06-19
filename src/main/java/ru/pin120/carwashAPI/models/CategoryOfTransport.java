package ru.pin120.carwashAPI.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Модель категорий транспорта
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "categories_of_transport")
public class CategoryOfTransport {

    /**
     * id категории
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long catTrId;

    /**
     * Название
     */
    @Column(unique = true,nullable = false,length = 50)
    @Size(max = 50, message = "Максимальная длина 50 символов")
    @NotBlank(message = "Необходимо ввести название категории автомобилей")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9 -]+$", message = "Допустимые символы для названия: латинские буквы, кириллица, цифры, пробелы и знаки тире")
    private String catTrName;

    /**
     * Список транспорта
     */
    @OneToMany(mappedBy = "categoryOfTransport")
    @JsonIgnore
    private List<Transport> transports;


    /**
     * Список позиций в прайс-листе
     */
    @OneToMany(mappedBy = "categoryOfTransport", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PriceList> priceListPositions;

}
