package com.peerisland.orders.dto.order;

public record OrderDto(
        Long addressId,
        Long branchId,
        String orderType,
        String paymentMethod,
        Double loyaltyPointUsed,
        Boolean isDineInOrder,
        Long last4,
        Long carId,
        String couponCode,
        AddressDto address) {

    @Override
    public Double loyaltyPointUsed() {
        return Math.round(loyaltyPointUsed * 100.0) / 100.0;
    }
}
