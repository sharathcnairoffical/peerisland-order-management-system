package com.peerisland.orders.dto.order;


import java.util.List;

public record OrderDetailsDto(
        Long productId,
        String name,
        Integer quantity,
        Double productPrice,
        String sizeModName,
        Double sizeModPrice,
        String toppingModName,
        Double toppingModPrice,
        String premiumModName,
        Double premiumModPrice,
        List<OrderDetailsModDto> modifiers,
        String plu
) {
}
