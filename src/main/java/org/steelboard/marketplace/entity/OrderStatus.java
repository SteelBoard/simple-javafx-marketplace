package org.steelboard.marketplace.entity;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED,
    FAILED;

    public static OrderStatus getOrderStatus(String orderStatus) {
        return switch (orderStatus) {
            case "PENDING" -> OrderStatus.PENDING;
            case "CONFIRMED" -> OrderStatus.CONFIRMED;
            case "PROCESSING" -> OrderStatus.PROCESSING;
            case "SHIPPED" -> OrderStatus.SHIPPED;
            case "DELIVERED" -> OrderStatus.DELIVERED;
            case "CANCELLED" -> OrderStatus.CANCELLED;
            case "REFUNDED" -> OrderStatus.REFUNDED;
            case "FAILED" -> OrderStatus.FAILED;
            default -> null;
        };
    }
}
