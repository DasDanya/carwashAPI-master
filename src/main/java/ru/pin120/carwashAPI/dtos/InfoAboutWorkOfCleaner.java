package ru.pin120.carwashAPI.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InfoAboutWorkOfCleaner {
    private LocalDate day;
    private int countBookings;
    private int totalPrice;

}
