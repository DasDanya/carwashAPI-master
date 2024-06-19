package ru.pin120.carwashAPI.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

/**
 * Модель услуги
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "services")
public class Service {

    /**
     * Название
     */
    @Id
    @Column(unique = true,nullable = false, length = 30)
    @Size(max = 30, message = "Максимальная длина 30 символов")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9 -]+$", message = "Допустимые символы для названия: латинские буквы, кириллица, цифры, пробелы и знаки тире")
    @NotBlank(message = "Необходимо ввести название услуги")
    private String servName;

    /**
     * Категория
     */
    @ManyToOne
    @NotNull(message = "Необходимо указать категорию услуг")
    @JoinColumn(name="cat_name", nullable = false)
    @JsonBackReference
    private CategoryOfServices category;

    /**
     * Позиции в прайс-листе
     */
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PriceList> priceList;

    /**
     * Необходимые категории расходных материалов
     */
    @ManyToMany
    @JoinTable(name = "cat_of_supplies_for_service",
            joinColumns = @JoinColumn(name = "serv_name",nullable = false),
            inverseJoinColumns = @JoinColumn(name = "c_sup_name",nullable = false),
            uniqueConstraints = @UniqueConstraint(columnNames = {"serv_name", "c_sup_name"}))
    private List<CategoryOfSupplies> categoriesOfSupplies;

    /**
     * Список заказов
     */
    @ManyToMany(mappedBy = "services")
    @JsonIgnore
    private List<Booking> bookings;

    /**
     * Конструктор
     * @param servName Название
     * @param category Категория
     */
    public Service(String servName, CategoryOfServices category) {
        this.servName = servName;
        this.category = category;
    }
}
