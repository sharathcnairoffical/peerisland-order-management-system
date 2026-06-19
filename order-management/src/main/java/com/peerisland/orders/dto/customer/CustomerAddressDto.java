package com.peerisland.orders.dto.customer;

public record CustomerAddressDto(
        Long addressId,
        Long customerId,
        String address,
        String addressType,
        String buildingNameNumber,
        String floor
) {
}
