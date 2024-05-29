package ru.pin120.carwashAPI.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "boxes")
public class Box {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boxId;

    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Необходимо указать статус")
    private BoxStatus boxStatus;


    @OneToMany(mappedBy = "box", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<WorkSchedule> workSchedules;

    @OneToMany(mappedBy = "box", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SuppliesInBox> suppliesInBoxes;

}
