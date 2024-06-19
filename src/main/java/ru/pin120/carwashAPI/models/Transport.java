package ru.pin120.carwashAPI.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
 * Модель транспорта
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transport",uniqueConstraints = @UniqueConstraint(columnNames = {"tr_mark","tr_model","cat_tr_id"}))

public class Transport {

    /**
     * id транспорта
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trId;

    /**
     * Марка
     */
    @Column(name = "tr_mark",nullable = false,length = 50)
    @NotBlank(message = "Необходимо ввести марку автомобиля")
    @Size(max = 50, message = "Максимальная длина 50 символов")
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\s-]+$",message = "Разрешается вводить русские, английские буквы, тире и пробелы")
    private String trMark;

    /**
     * Модель
     */
    @Column(name = "tr_model",nullable = false,length = 50)
    @NotBlank(message = "Необходимо ввести модель автомобиля")
    @Size(max = 50, message = "Максимальная длина 50 символов")
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё0-9\\s-]+$", message = "Разрешается вводить русские, английские буквы, цифры, тире, одинарные кавычки и пробелы")
    private String trModel;

    /**
     * Категория
     */
    @ManyToOne()
    @NotNull(message = "Необходимо указать категорию автомобиля")
    @JoinColumn(name = "cat_tr_id",nullable = false)
    private CategoryOfTransport categoryOfTransport;

    /**
     * Список транспорта клиентов
     */
    @OneToMany(mappedBy = "transport")
    @JsonIgnore
    private List<ClientsTransport> clientsTransport;
}
