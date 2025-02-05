package com.company.utils;

import com.company.core.ErrorHandler;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.company.crypto.utils.CryptoInputValidator.validateInput;

/**
 * Utility class for Base64 encoding and decoding operations.
 * Provides methods for handling strings, byte arrays, and secret keys with proper validation and error handling.
 */
public final class Base64Utils {
    private static final Logger logger = LoggerUtils.getLogger(Base64Utils.class);

    private static final String BYTE_ARRAY_PARAMETER = "Byte array";
    private static final String STRING_PARAMETER = "String";
    private static final String ENCODED_KEY_PARAMETER = "Encoded key";
    private static final String ENVIRONMENT_SECRET_PARAMETER = "Environment SecretSecret Key Variables";

    private Base64Utils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    /**
     * Encodes a byte array to Base64 string.
     *
     * @param data the byte array to encode
     * @return the Base64 encoded string
     * @throws IllegalArgumentException if the input is null
     */
    public static String encodeArray(byte[] data) {
        validateInput(data, BYTE_ARRAY_PARAMETER);
        try {
            return Base64.getEncoder().encodeToString(data);
        } catch (Exception error) {
           ErrorHandler.logError(error, "encodeArray", "Failed to encode byte array to base64");
           throw new RuntimeException(error);
        }
    }

    /**
     * Decodes a Base64 string to byte array.
     *
     * @param base64String the Base64 string to decode
     * @return the decoded byte array
     * @throws IllegalArgumentException if the input is null or invalid Base64
     */
    public static byte[] decodeToArray(String base64String) {
        validateInput(base64String, STRING_PARAMETER);
        try {
            return Base64.getDecoder().decode(base64String);
        } catch (Exception error) {
            ErrorHandler.logError(error, "decodeToArray", "Failed to decode base64 to byte array");
            throw new RuntimeException(error);
        }
    }

    /**
     * Encodes a string to Base64.
     *
     * @param data the string to encode
     * @return the Base64 encoded string
     * @throws IllegalArgumentException if the input is null
     */
    public static String encodeString(String data) {
        validateInput(data, STRING_PARAMETER);
        try {
            return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception error) {
            ErrorHandler.logError(error, "encodeString", "Failed to encode string to base64");
            throw new RuntimeException(error);
        }
    }

    /**
     * Decodes a Base64 string back to a regular string.
     *
     * @param base64String the Base64 string to decode
     * @return the decoded string
     * @throws IllegalArgumentException if the input is null or invalid Base64
     */
    public static String decodeToString(String base64String) {
        validateInput(base64String, STRING_PARAMETER);
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception error) {
           ErrorHandler.logError(error, "decodeToString", "Failed to decode base64 to string");
           throw new RuntimeException(error);
        }
    }

    /**
     * Encodes a EnvironmentSecretKeyVariables to Base64 string.
     *
     * @param secretKey the EnvironmentSecretKeyVariables to encode
     * @return the Base64 encoded string
     * @throws IllegalArgumentException if the input is null
     */
    public static String encodeSecretKey(SecretKey secretKey) {
        validateInput(secretKey, ENVIRONMENT_SECRET_PARAMETER);
        try {
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception error) {
            ErrorHandler.logError(error, "encodeSecretKey", "Failed to encode secret key");
            throw new RuntimeException(error);
        }
    }

    /**
     * Decodes a Base64 string to a EnvironmentSecretKeyVariables.
     *
     * @param encodedKey the Base64 encoded key string
     * @return the decoded EnvironmentSecretKeyVariables
     * @throws IllegalArgumentException if the input is null or invalid Base64
     */
    public static SecretKey decodeSecretKey(String encodedKey) {
        validateInput(encodedKey, ENCODED_KEY_PARAMETER);
        try {
            byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
            return new SecretKeySpec(decodedKey, "");
        } catch (Exception error) {
            ErrorHandler.logError(error, "decodeSecretKey", "Failed to decode secret key");
            throw new RuntimeException(error);
        }
    }
}