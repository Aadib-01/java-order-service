package com.example.demo;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
    UUID id, String productName, int quantity,
    BigDecimal price, OrderStatus status, Instant createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getId(), order.getProductName(),
            order.getQuantity(), order.getPrice(), order.getStatus(), order.getCreatedAt());
    }
}