package ru.pin120.carwashAPI.models;

import lombok.Getter;

@Getter
/**
 * Статусы бокса
 */
public enum BoxStatus {

    /**
     * Доступен
     */
    AVAILABLE,
    /**
     * Закрыт
     */
    CLOSED

}
