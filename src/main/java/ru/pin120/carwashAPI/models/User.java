package ru.pin120.carwashAPI.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usId;

    @Column(nullable = false, unique = true, length = 30)
    @NotBlank(message = "Необходимо ввести имя пользователя")
    @Pattern(regexp = "^[a-zA-Z0-9]{5,}$", message = "Имя пользователя должно состоять минимум из 5 символов. В нем должны быть только символы латиницы и цифры (пробелы не допустимы)")
    @Size(min = 5, max = 30, message = "Минимальная длина имени пользователя = 5, максимальная = 30")
    private String usName;

    @Column(nullable = false)
    @NotBlank(message = "Необходимо ввести пароль")
    @JsonIgnore
    private String usPassword;

    @Column(nullable = false,length = 20)
    @NotNull(message = "Необходимо указать роль пользователя")
    @Enumerated(EnumType.STRING)
    private UserRole usRole;

}
