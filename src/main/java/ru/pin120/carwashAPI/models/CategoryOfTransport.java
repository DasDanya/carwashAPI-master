package ru.pin120.carwashAPI.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "categories_of_transport")
public class CategoryOfTransport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long catTrId;

    @Column(unique = true,nullable = false,length = 50)
    @Size(max = 30, message = "Максимальная длина 50 символов")
    @NotBlank(message = "Необходимо ввести название категории автомобилей")
    //@Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9 ]+$", message = "Категория должна состоять из русских или английских букв и цифр")
    private String catTrName;

    @OneToMany(mappedBy = "categoryOfTransport")
    @JsonIgnore
    private List<Transport> cars;


    @OneToMany(mappedBy = "categoryOfTransport", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PriceList> priceList;

}
