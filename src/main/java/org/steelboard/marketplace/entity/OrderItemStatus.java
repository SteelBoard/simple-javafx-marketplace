package org.steelboard.marketplace.entity;

public enum OrderItemStatus {
    PROCESSING,   // заказ поступил, ожидание решения продавца
    CONFIRMED,    // продавец принял позицию (в наличии)
    CANCELLED,    // продавец отклонил позицию
    SHIPPED,      // продавец отправил
    DELIVERED,
    REFUNDED;// покупатель подтвердил получение
}
