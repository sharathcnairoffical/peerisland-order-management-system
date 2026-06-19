package com.peerisland.orders.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    // Password must contain at least:
    // - 1 uppercase letter
    // - 1 lowercase letter
    // - 1 digit
    // - 1 special character
    // - minimum 8 characters
    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.trim().isEmpty()) {
            return true; // Let @NotBlank handle null/empty validation
        }

        boolean isValid = pattern.matcher(password).matches();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Password must contain at least 8 characters including: 1 uppercase letter, 1 lowercase letter, 1 digit, and 1 special character (@$!%*?&)")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
