package com.peerisland.orders.dto.order;

public record OrderItemResponse(
        Long orderDetailId,
        Long productId,
        String name,
        Integer quantity,
        Double productPrice,
        Double lineTotal,
        String imageUrl
) {
}
