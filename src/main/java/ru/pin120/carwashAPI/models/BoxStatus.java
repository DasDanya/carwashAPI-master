package ru.pin120.carwashAPI.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
//@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BoxStatus {
//    AVAILABLE("Доступен"),
//    REPAIR("Закрыт");
//    private final String displayValue;
//
//    BoxStatus(String displayValue) {
//        this.displayValue = displayValue;
//    }

    AVAILABLE,
    REPAIR

}
