package com.peerisland.orders.dto.customer;

import jakarta.validation.constraints.*;
import com.peerisland.orders.validation.ValidPassword;

public record CustomerCreationRequest(

        @NotBlank(message = "{validation.name.required}")
        @Size(min = 2, max = 50, message = "{validation.name.size}")
        String name,

        @NotBlank(message = "{validation.email.required}")
        @Email(message = "{validation.email.invalid}")
        String email,

        @NotBlank(message = "{validation.role.required}")
        String role,

        @NotBlank(message = "{validation.phoneNumber.required}")
        @Pattern(regexp = "^(\\+|%2B)?\\d{10,15}$", message = "{validation.phoneNumber.pattern}")
        String mobileNumber,

        String countryCode,

        @NotBlank(message = "{validation.password.required}")
        @ValidPassword
        String password
){
}
