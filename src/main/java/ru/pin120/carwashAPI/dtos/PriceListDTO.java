package ru.pin120.carwashAPI.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PriceListDTO {

    private Long plId;
    private String servName;
    private String catTrName;

    private Integer plTime;
    private Integer plPrice;
}
