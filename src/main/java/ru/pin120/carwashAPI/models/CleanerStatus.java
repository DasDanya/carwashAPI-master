package ru.pin120.carwashAPI.models;

import lombok.Getter;

/**
 * Статусы мойщика
 */
@Getter
public enum CleanerStatus {

    /**
     * Работает
     */
    ACT,
    /**
     * Уволен
     */
    DISMISSED

}
