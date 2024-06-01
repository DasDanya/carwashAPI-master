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
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "cleaners")
public class Cleaner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clrId;

    @Column(nullable = false,length = 50)
    @Size(max = 50, message = "Максимальная длина фамилии = 50 символов")
    @Pattern(regexp = "^[А-ЯЁа-яё-]+$", message = "Некорректный ввод фамилии")
    @NotBlank(message = "Необходимо ввести фамилию сотрудника")
    private String clrSurname;


    @Column(nullable = false, length = 50)
    @Size(max = 50, message = "Максимальная длина имени = 50 символов")
    @Pattern(regexp = "^[А-ЯЁа-яё-]+$", message = "Некорректный ввод имени")
    @NotBlank(message = "Необходимо ввести имя сотрудника")
    private String clrName;

    @Column(length = 50)
    @Size(max = 50, message = "Максимальная длина имени = 50 символов")
    @Pattern(regexp = "^(|[А-ЯЁа-яё-]+)$", message = "Некорректный ввод отчества")
    private String clrPatronymic;

    @Column(nullable = false, length = 12)
    @Size(max = 12, message = "Максимальная длина = 12 символов")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Неверный формат номера телефона. Пример +77777777777")
    private String clrPhone;

    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Необходимо указать статус")
    private CleanerStatus clrStatus;

    @Column(length = 20)
    private String clrPhotoName;

    @OneToMany(mappedBy = "cleaner", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<WorkSchedule> workSchedules;

    @OneToMany(mappedBy = "cleaner")
    @JsonIgnore
    private List<Booking> bookings;
}
