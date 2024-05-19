package ru.pin120.carwashAPI.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clId;

    @Column(nullable = false,length = 50)
    @Size(max = 50, message = "Максимальная длина фамилии = 50 символов")
    @Pattern(regexp = "^[А-ЯЁа-яё-]+$", message = "Некорректный ввод фамилии")
    @NotBlank(message = "Необходимо ввести фамилию клиента")
    private String clSurname;

    @Column(nullable = false, length = 50)
    @Size(max = 50, message = "Максимальная длина имени = 50 символов")
    @Pattern(regexp = "^[А-ЯЁа-яё-]+$", message = "Некорректный ввод имени")
    @NotBlank(message = "Необходимо ввести имя клиента")
    private String clName;

    @Column(nullable = false, length = 12)
    @Size(max = 12, message = "Максимальная длина = 12 символов")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Неверный формат номера телефона. Пример +77777777777")
    private String clPhone;

    @Column(nullable = false)
    @NotNull(message = "Необходимо указать скидку")
    @Max(value = 100, message = "Значение должно быть меньше или равно 100")
    private Integer clDiscount;


    @OneToMany(mappedBy = "client")
    @JsonIgnore
    private List<ClientsTransport> transports;
}
