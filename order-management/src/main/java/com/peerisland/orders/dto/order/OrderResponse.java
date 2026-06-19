package com.peerisland.orders.dto.order;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long orderId,
        String orderUniqueId,
        Long customerId,
        String status,
        Long addressId,
        Integer orderType,
        Long branchId,
        String branchUniqueId,
        Double orderAmount,
        String specialNotes,
        LocalDateTime addDate,
        LocalDateTime modDate,
        LocalDateTime orderCompleteDate,
        List<OrderItemResponse> items
) {
}
