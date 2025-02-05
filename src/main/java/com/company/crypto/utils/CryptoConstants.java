package com.company.crypto.utils;

public enum CryptoConstants {

    AES_ALGORITHM("AES"),
    AES_CBC_PKCS5("AES/CBC/PKCS5Padding"),
    PBKDF2("PBKDF2WithHmacSHA256"),
    HMAC_SHA256("HmacSHA256"),
    ARGON2_ITERATIONS(3),
    ARGON2_MEMORY(65536),
    ARGON2_PARALLELISM(4),
    AES_SECRET_KEY_SIZE(32),
    IV_KEY_SIZE(16),
    SALT_KEY_SIZE(32),
    HMAC_KEY_SIZE(32);

    private final Object value;

    CryptoConstants(Object value) {
        this.value = value;
    }

    public String getStringValue() {
        if (value instanceof String) {
            return (String) value;
        }
        throw new IllegalStateException("Value is not a String: " + this);
    }

    public int getIntValue() {
        if (value instanceof Integer) {
            return (Integer) value;
        }
        throw new IllegalStateException("Value is not an Integer: " + this);
    }
}
