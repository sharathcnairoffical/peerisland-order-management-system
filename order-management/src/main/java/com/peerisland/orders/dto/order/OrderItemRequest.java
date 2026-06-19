package com.peerisland.orders.dto.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest(
        @NotNull(message = "productId is required")
        Long productId,

        @NotBlank(message = "Item name is required")
        String name,

        @NotNull(message = "quantity is required")
        @Positive(message = "quantity must be greater than zero")
        Integer quantity,

        @NotNull(message = "productPrice is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "productPrice must be greater than zero")
        Double productPrice,

        String imageUrl
) {
}
