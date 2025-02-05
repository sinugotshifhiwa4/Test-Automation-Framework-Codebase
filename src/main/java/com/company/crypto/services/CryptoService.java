package com.company.crypto.services;

import com.company.core.ErrorHandler;
import com.company.crypto.utils.CryptoConstants;
import com.company.utils.Base64Utils;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

import static com.company.crypto.services.SecureKeyGenerator.generateIv;
import static com.company.crypto.services.SecureKeyGenerator.generateSalt;
import static com.company.crypto.utils.CryptoInputValidator.validateInput;

public class CryptoService {

    private static final String SECRET_KEY_INPUT_TYPE = "Secret Key";
    private static final String DATA_INPUT_TYPE = "Data";
    private static final String ENCRYPTED_DATA_INPUT_TYPE = "Encrypted Data";

    private CryptoService() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    private record EncryptionComponents(byte[] salt, byte[] iv, byte[] cipherText, byte[] mac) {
        public byte[] combine() {
            return ByteBuffer.allocate(salt.length + iv.length + cipherText.length + mac.length)
                    .put(salt)
                    .put(iv)
                    .put(cipherText)
                    .put(mac)
                    .array();
        }

        public static EncryptionComponents extract(byte[] combined) {
            int saltSize = CryptoConstants.SALT_KEY_SIZE.getIntValue();
            int ivSize = CryptoConstants.IV_KEY_SIZE.getIntValue();
            int macSize = CryptoConstants.HMAC_KEY_SIZE.getIntValue();
            int cipherTextSize = combined.length - saltSize - ivSize - macSize;

            // Check if the combined array is of the expected size
            if (combined.length < saltSize + ivSize + macSize) {
                throw new IllegalArgumentException("Combined byte array is too short.");
            }

            ByteBuffer buffer = ByteBuffer.wrap(combined);
            byte[] salt = new byte[saltSize];
            byte[] iv = new byte[ivSize];
            byte[] cipherText = new byte[cipherTextSize];
            byte[] mac = new byte[macSize];

            try {
                buffer.get(salt);
                buffer.get(iv);
                buffer.get(cipherText);
                buffer.get(mac);
            } catch (BufferUnderflowException error) {
                throw new IllegalArgumentException("Combined byte array does not contain enough data.", error);
            }

            return new EncryptionComponents(salt, iv, cipherText, mac);
        }
    }

    // region Encryption/Decryption Methods
    public static String encrypt(SecretKey key, String data) throws CryptoException {
        validateInput(key, SECRET_KEY_INPUT_TYPE);
        validateInput(data, DATA_INPUT_TYPE);

        try {
            byte[] salt = generateSalt();
            byte[] iv = generateIv();
            SecretKeySpec derivedKey = deriveKey(new String(key.getEncoded(), StandardCharsets.UTF_8), salt);

            Cipher cipher = initializeCipher(iv, derivedKey, Cipher.ENCRYPT_MODE);
            byte[] cipherText = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            byte[] mac = generateMac(salt, iv, cipherText, derivedKey.getEncoded());

            EncryptionComponents components = new EncryptionComponents(salt, iv, cipherText, mac);
            return Base64Utils.encodeArray(components.combine());
        } catch (Exception error) {
            ErrorHandler.logError(error, "encrypt", "Failed to encrypt data");
            throw new CryptoException("Encryption failed", error);
        }
    }

    public static String decrypt(SecretKey key, String encryptedData) throws CryptoException {
        validateInput(key, SECRET_KEY_INPUT_TYPE);
        validateInput(encryptedData, ENCRYPTED_DATA_INPUT_TYPE);

        try {
            byte[] combined = Base64Utils.decodeToArray(encryptedData);
            EncryptionComponents components = EncryptionComponents.extract(combined);

            SecretKeySpec derivedKey = deriveKey(new String(key.getEncoded(), StandardCharsets.UTF_8),
                    components.salt());

            verifyMac(components, derivedKey);
            Cipher cipher = initializeCipher(components.iv(), derivedKey, Cipher.DECRYPT_MODE);
            byte[] decryptedBytes = cipher.doFinal(components.cipherText());

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception error) {
            ErrorHandler.logError(error, "decrypt", "Failed to decrypt data");
            throw new CryptoException("Decryption failed", error);
        }
    }

    private static SecretKeySpec deriveKey(String secretKey, byte[] salt) {
        try {
            Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                    .withSalt(salt)
                    .withIterations(CryptoConstants.ARGON2_ITERATIONS.getIntValue())
                    .withMemoryAsKB(CryptoConstants.ARGON2_MEMORY.getIntValue())
                    .withParallelism(CryptoConstants.ARGON2_MEMORY.getIntValue())
                    .build();

            Argon2BytesGenerator generator = new Argon2BytesGenerator();
            generator.init(params);

            byte[] result = new byte[CryptoConstants.AES_SECRET_KEY_SIZE.getIntValue()];
            generator.generateBytes(secretKey.getBytes(StandardCharsets.UTF_8), result);

            return new SecretKeySpec(result, CryptoConstants.AES_ALGORITHM.getStringValue());
        } catch (Exception error) {
            ErrorHandler.logError(error, "deriveKey", "Failed to derive key");
            throw new IllegalStateException("Failed to derive key", error);
        } finally {
            Arrays.fill(secretKey.toCharArray(), '\0');
        }
    }

    private static Cipher initializeCipher(byte[] iv, SecretKeySpec key, int mode) throws Exception {
        try{
            Cipher cipher = Cipher.getInstance(CryptoConstants.AES_CBC_PKCS5.getStringValue());
            cipher.init(mode, key, new IvParameterSpec(iv));
            return cipher;
        } catch (Exception error) {
            ErrorHandler.logError(error, "initializeCipher", "Failed to initialize cipher");
            throw new RuntimeException(error);
        }
    }

    private static byte[] generateMac(byte[] salt, byte[] iv, byte[] cipherText, byte[] key) throws Exception {
        try{
            String macSha256= CryptoConstants.HMAC_SHA256.getStringValue();
            Mac mac = Mac.getInstance(macSha256);
            mac.init(new SecretKeySpec(key, macSha256));
            return mac.doFinal(ByteBuffer.allocate(salt.length + iv.length + cipherText.length)
                    .put(salt).put(iv).put(cipherText).array());
        } catch (Exception error) {
            ErrorHandler.logError(error, "generateMac", "Failed to generate MAC");
            throw new RuntimeException(error);
        }
    }

    private static void verifyMac(EncryptionComponents components, SecretKeySpec key) throws Exception {
        try {
            byte[] computedMac = generateMac(components.salt(), components.iv(),
                    components.cipherText(), key.getEncoded());
            if (!MessageDigest.isEqual(components.mac(), computedMac)) {
                throw new SecurityException("MAC verification failed - data may be tampered");
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "verifyMac", "Failed to verify MAC");
            throw new RuntimeException(error);
        }
    }
}
