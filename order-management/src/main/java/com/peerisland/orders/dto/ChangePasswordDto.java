package com.peerisland.orders.dto;

import com.peerisland.orders.validation.PasswordMatch;
import com.peerisland.orders.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@PasswordMatch
public record ChangePasswordDto(

        @NotBlank(message = "{validation.password.required}")
        String oldPassword,

        @NotBlank(message = "{validation.password.required}")
        @Size(min = 8, max = 20, message = "{validation.password.size}")
        @ValidPassword
        String newPassword,

        @NotBlank(message = "{validation.confirmPassword.required}")
        String confirmPassword
) {
}
