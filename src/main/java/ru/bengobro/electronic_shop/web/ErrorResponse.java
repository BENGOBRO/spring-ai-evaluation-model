package ru.bengobro.electronic_shop.web;

/** Единый формат тела ответа при ошибке. */
public record ErrorResponse(int status, String error, String message) {
}
