package ru.pin120.carwashAPI.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Table(name = "services", uniqueConstraints = @UniqueConstraint(columnNames = "serv_name"))
@Table(name = "services")
public class Service {

    @Id
    @Column(unique = true,nullable = false, length = 30)
    @Size(max = 30, message = "Максимальная длина 30 символов")
    @NotBlank(message = "Необходимо ввести название услуги")
    private String servName;

    @ManyToOne
    @NotNull(message = "Необходимо указать категорию услуг")
    @JoinColumn(name="cat_name", nullable = false)
    @JsonBackReference
    private CategoryOfServices category;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PriceList> priceList;

    public Service(String servName, CategoryOfServices category) {
        this.servName = servName;
        this.category = category;
    }
}
