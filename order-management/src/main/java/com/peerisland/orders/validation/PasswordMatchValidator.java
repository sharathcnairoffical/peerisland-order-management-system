package com.peerisland.orders.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return true;
        }

        try {
            // Get password and confirmPassword values using reflection
            String password = getFieldValue(object, "password");
            String confirmPassword = getFieldValue(object, "confirmPassword");

            boolean isValid = password != null && password.equals(confirmPassword);

            if (!isValid) {
                // Disable default violation
                context.disableDefaultConstraintViolation();
                // Add custom violation with field-specific error
                context.buildConstraintViolationWithTemplate("{validation.password.match}")
                        .addPropertyNode("confirmPassword")
                        .addConstraintViolation();
            }

            return isValid;
        } catch (Exception e) {
            // If reflection fails, return false and log error
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password validation failed: " + e.getMessage())
                    .addConstraintViolation();
            return false;
        }
    }

    private String getFieldValue(Object object, String fieldName) throws Exception {
        // Try to find the method (for records, fields are accessed via methods)
        Method method = object.getClass().getMethod(fieldName);
        Object value = method.invoke(object);
        return value != null ? value.toString() : null;
    }
}
