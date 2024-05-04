package ru.pin120.carwashAPI.models;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transport",uniqueConstraints = @UniqueConstraint(columnNames = {"tr_mark","tr_model","cat_tr_id"}))
public class Transport {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trId;

    @Column(name = "tr_mark",nullable = false,length = 50)
    @NotBlank(message = "Необходимо ввести марку автомобиля")
    @Size(max = 50, message = "Максимальная длина 50 символов")
    private String trMark;

    @Column(name = "tr_model",nullable = false,length = 50)
    @NotBlank(message = "Необходимо ввести модель автомобиля")
    @Size(max = 50, message = "Максимальная длина 50 символов")
    private String trModel;


    @ManyToOne()
    @NotNull(message = "Необходимо указать категорию автомобиля")
    @JoinColumn(name = "cat_tr_id",nullable = false)
    @JsonManagedReference
    private CategoryOfTransport categoryOfTransport;

}
