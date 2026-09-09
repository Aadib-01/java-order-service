package com.example.demo;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(
    @NotBlank String productName,
    @Min(1) int quantity,
    @Positive BigDecimal price
) {}
