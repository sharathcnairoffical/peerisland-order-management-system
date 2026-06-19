package com.peerisland.orders.dto.order;

public record AddressDto(
        Long addressId,
        String address,
        String addressType,
        String buildingNameNumber,
        String floor
) {
}
