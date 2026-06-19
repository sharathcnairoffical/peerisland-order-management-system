package com.peerisland.orders.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderCreationRequest(
        @NotNull(message = "customerId is required")
        Long customerId,

        Long addressId,

        Integer orderType,

        Long branchId,

        String branchUniqueId,

        String specialNotes,

        @Valid
        @NotEmpty(message = "Order items are required")
        List<OrderItemRequest> items
) {
}
