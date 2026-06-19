package com.peerisland.orders.dto.order;

import jakarta.validation.constraints.NotBlank;

public record OrderStatusUpdateRequest(
        @NotBlank(message = "status is required")
        String status
) {
}
