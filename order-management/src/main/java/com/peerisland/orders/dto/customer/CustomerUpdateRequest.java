package com.peerisland.orders.dto.customer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.peerisland.orders.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomerUpdateRequest(
        String storeId,

        @Size(min = 2, max = 50, message = "{validation.name.size}")
        String name,

        @Email(message = "{validation.email.invalid}")
        String email,

        String role,

        @Pattern(regexp = "^(\\+|%2B)?\\d{10,15}$", message = "{validation.phoneNumber.pattern}")
        String mobileNumber,

        String countryCode,

        Boolean active
) {
}
