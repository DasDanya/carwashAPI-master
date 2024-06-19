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
/**
 * Модель мойщика
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "cleaners")
public class Cleaner {

    /**
     * id мойщика
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clrId;

    /**
     * Фамилия
     */
    @Column(nullable = false,length = 50)
    @Size(max = 50, message = "Максимальная длина фамилии = 50 символов")
    @Pattern(regexp = "^[А-ЯЁа-яё-]+$", message = "Некорректный ввод фамилии")
    @NotBlank(message = "Необходимо ввести фамилию сотрудника")
    private String clrSurname;

    /**
     * Имя
     */
    @Column(nullable = false, length = 50)
    @Size(max = 50, message = "Максимальная длина имени = 50 символов")
    @Pattern(regexp = "^[А-ЯЁа-яё-]+$", message = "Некорректный ввод имени")
    @NotBlank(message = "Необходимо ввести имя сотрудника")
    private String clrName;

    /**
     * Отчество
     */
    @Column(length = 50)
    @Size(max = 50, message = "Максимальная длина имени = 50 символов")
    @Pattern(regexp = "^(|[А-ЯЁа-яё-]+)$", message = "Некорректный ввод отчества")
    private String clrPatronymic;

    /**
     * Номер телефона
     */
    @Column(nullable = false, length = 12)
    @Size(max = 12, message = "Максимальная длина = 12 символов")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Неверный формат номера телефона. Пример +77777777777")
    private String clrPhone;

    /**
     * Статус
     */
    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Необходимо указать статус")
    private CleanerStatus clrStatus;

    /**
     * Название фотографии
     */
    @Column(length = 20)
    private String clrPhotoName;

    /**
     * Список рабочих дней
     */
    @OneToMany(mappedBy = "cleaner", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<WorkSchedule> workSchedules;

    /**
     * Список заказов
     */
    @OneToMany(mappedBy = "cleaner")
    @JsonIgnore
    private List<Booking> bookings;
}
