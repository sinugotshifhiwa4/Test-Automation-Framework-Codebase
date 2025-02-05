package com.company.crypto.services;

import com.company.core.ErrorHandler;
import com.company.crypto.utils.CryptoConstants;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

import static com.company.crypto.utils.CryptoInputValidator.validateKeySize;
import static com.company.crypto.utils.CryptoInputValidator.validateSize;

public class SecureKeyGenerator {

    private static final ThreadLocal<SecureRandom> SECURE_RANDOM = ThreadLocal.withInitial(SecureRandom::new);
    private static final String IV_PARAMETER = "IV";
    private static final String SALT_PARAMETER = "Salt";

    private SecureKeyGenerator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Generates a random IV with default size
     */
    public static byte[] generateIv() {
        try {
            return generateIv(CryptoConstants.IV_KEY_SIZE.getIntValue());
        } catch (Exception error) {
            ErrorHandler.logError(error, "generateIv", "Failed to generate IV");
            throw new RuntimeException(error);
        }
    }

    /**
     * Generates a random IV with specified size
     */
    public static byte[] generateIv(int size) {
        try {
            validateSize(size, IV_PARAMETER);
            return generateRandomBytes(size);
        } catch (Exception error) {
            ErrorHandler.logError(error, "generateIv", "Failed to generate IV");
            throw new RuntimeException(error);
        }
    }

    /**
     * Generates a random salt with default size
     */
    public static byte[] generateSalt() {
        try {
            return generateSalt(CryptoConstants.SALT_KEY_SIZE.getIntValue());
        } catch (Exception error) {
            ErrorHandler.logError(error, "generateSalt", "Failed to generate salt");
            throw new RuntimeException(error);
        }
    }

    /**
     * Generates a random salt with specified size
     */
    public static byte[] generateSalt(int size) {
        try {
            validateSize(size, SALT_PARAMETER);
            return generateRandomBytes(size);
        } catch (Exception error) {
            ErrorHandler.logError(error, "generateSalt", "Failed to generate salt");
            throw new RuntimeException(error);
        }
    }

    /**
     * Generates a new EnvironmentSecretKeyVariables with default size (256 bits/32 bytes)
     */
    public static SecretKey generateSecretKey() {
        try {
            return generateSecretKey(CryptoConstants.AES_SECRET_KEY_SIZE.getIntValue());
        } catch (Exception error) {
            ErrorHandler.logError(error, "generateSecretKey", "Failed to generate secret key");
            throw new RuntimeException(error);
        }
    }

    /**
     * Generates a new EnvironmentSecretKeyVariables with specified size
     */
    public static SecretKey generateSecretKey(int sizeInBytes) {
        try {
            validateKeySize(sizeInBytes);
            byte[] keyBytes = generateRandomBytes(sizeInBytes);
            return new SecretKeySpec(keyBytes, CryptoConstants.AES_ALGORITHM.getStringValue());
        } catch (Exception error) {
            ErrorHandler.logError(error, "generateSecretKey", "Failed to generate secret key");
            throw new RuntimeException(error);
        }
    }

    private static byte[] generateRandomBytes(int size) {
        try {
            if (size < 0) {
                throw new IllegalArgumentException("Size must be non-negative");
            }

            byte[] bytes = new byte[size];
            SECURE_RANDOM.get().nextBytes(bytes);
            return bytes;
        } catch (Exception error) {
            ErrorHandler.logError(error, "generateRandomBytes", "Failed to generate random bytes");
            throw new RuntimeException(error);
        }
    }

}
