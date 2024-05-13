package ru.pin120.carwashAPI.models;

import lombok.Getter;

@Getter
public enum Days {
    MONDAY("Понедельник"),
    TUESDAY("Вторник"),
    WEDNESDAY("Среда"),
    THURSDAY("Четверг"),
    FRIDAY("Пятница"),
    SATURDAY("Суббота"),
    SUNDAY("Воскресенье");

    private final String displayValue;

    Days(String displayValue) {
        this.displayValue = displayValue;
    }

}
