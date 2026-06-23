package ru.bengobro.electronic_shop.web;

/** Сущность не найдена — маппится в HTTP 404. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
