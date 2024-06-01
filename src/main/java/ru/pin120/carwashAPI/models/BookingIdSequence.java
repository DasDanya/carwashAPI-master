package ru.pin120.carwashAPI.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingIdSequence {
    @Id
    private int year;
    @Min(value = 1, message = "Минимальное значение id = 1")
    private int lastId;
}
