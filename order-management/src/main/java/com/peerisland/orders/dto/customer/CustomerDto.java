package com.peerisland.orders.dto.customer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomerDto (
        Long customerId,
        String storeId,
        String fullName,
        String email,
        String mobileNumber,
        String countryCode,
        RoleDto role,
        Boolean active,
        String status,
        String customerUniqueId,
        double loyaltyPoints,
        double loyaltyPointsValue) {

    @Override
    public double loyaltyPoints() {
        return Math.round(loyaltyPoints * 100.0) / 100.0;
    }

    @Override
    public double loyaltyPointsValue() {
        return Math.round(loyaltyPointsValue * 100.0) / 100.0;
    }

}
