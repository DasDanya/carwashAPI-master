package ru.pin120.carwashAPI.models;

/**
 * Статусы заказа
 */
public enum BookingStatus {

    /**
     * Бронь
     */
    BOOKED,
    /**
     * Отменен
     */
    CANCELLED,
    /**
     * Выполнен
     */
    DONE,
    /**
     * Выполняется
     */
    IN_PROGRESS,
    /**
     * Не выполнен
     */
    NOT_DONE

}
