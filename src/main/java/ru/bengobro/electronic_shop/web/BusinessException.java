package ru.bengobro.electronic_shop.web;

/** Нарушение бизнес-правила (нет товара на складе, оплата отменённого заказа и т.п.) — HTTP 400. */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
