package ru.pin120.carwashAPI.models;

import lombok.Getter;

@Getter
public enum CleanerStatus {

    WORKING("Работает"),
    DISMISSED("Уволен");

    private final String displayValue;

    CleanerStatus(String displayValue) {
        this.displayValue = displayValue;
    }

}