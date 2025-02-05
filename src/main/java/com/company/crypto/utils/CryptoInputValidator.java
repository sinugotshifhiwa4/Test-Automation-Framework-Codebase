package com.company.crypto.utils;

import com.company.core.ErrorHandler;

public class CryptoInputValidator {

    private CryptoInputValidator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Validates that the input is not null or empty.
     *
     * @param input the input to validate
     * @param paramName the name of the parameter for error messages
     * @throws IllegalArgumentException if the input is null or empty
     */
    public static void validateInput(Object input, String paramName) {
        try {
            if (input == null || (input instanceof String s && s.isEmpty()) ||
                    (input instanceof byte[] b && b.length == 0)) {
                throw new IllegalArgumentException(paramName + " cannot be null or empty");
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "validateInput", "Failed to validate input");
            throw new RuntimeException(error);
        }
    }

    public static void validateSize(int size, String parameter) {
        try {
            if (size <= 0) {
                throw new IllegalArgumentException(parameter + " size must be positive");
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "validateSize", "Failed to validate size");
            throw new RuntimeException(error);
        }
    }

    public static void validateKeySize(int sizeInBytes) {
        try {
            if (sizeInBytes != 16 && sizeInBytes != 24 && sizeInBytes != 32) {
                throw new IllegalArgumentException("AES key size must be 16, 24, or 32 bytes");
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "validateKeySize", "Failed to validate key size");
            throw new RuntimeException(error);
        }
    }
}
