package ru.pin120.carwashAPI.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Pattern(regexp = "^[А-ЯЁ][а-яё\\s]*(-[А-ЯЁ][а-яё\\s]*)*$", message = "Некорректный ввод фамилии")
    @NotBlank(message = "Необходимо ввести фамилию клиента")
    private String clSurname;

    @Column(nullable = false, length = 50)
    @Size(max = 50, message = "Максимальная длина имени = 50 символов")
    @Pattern(regexp = "^[А-ЯЁ][а-яё]*$", message = "Некорректный ввод имени")
    @NotBlank(message = "Необходимо ввести имя клиента")
    private String clName;

    @Column(unique = true,nullable = false, length = 18)
    @Size(max = 18, message = "Максимальная длина = 18 символам")
    @Pattern(regexp = "^\\+7 \\(\\d{3}\\) \\d{3}-\\d{2}-\\d{2}$", message = "Неверный формат номера телефона. Пример +7 (777) 777-77-77")
    private String clPhone;

    @Max(value = 100, message = "Значение должно быть меньше или равно 100")
    private Integer clDiscount;

    @OneToMany(mappedBy = "client")
    @JsonIgnore
    private List<ClientsTransport> transports;
}
