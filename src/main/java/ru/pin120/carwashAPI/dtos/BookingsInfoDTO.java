package ru.pin120.carwashAPI.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingsInfoDTO {

    private int totalCount;
    private int totalPrice;
}
