package com.peerisland.orders.dto.customer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomerDto (
        Long customerId,
        String fullName,
        String email,
        String mobileNumber,
        RoleDto role,
        Boolean active,
        String status,
        String customerUniqueId) {

}
