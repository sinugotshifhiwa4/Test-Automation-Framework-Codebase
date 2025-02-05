package com.company.configurations.environments;

import com.company.core.ErrorHandler;
import com.company.utils.Base64Utils;
import com.company.utils.LoggerUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Map;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EnvironmentConfigManager {

    private static final Logger logger = LoggerUtils.getLogger(EnvironmentConfigManager.class);

    /**
     * Thread-safe cache for storing loaded EnvironmentConfigManager instances.
     */
    private static final Map<String, EnvironmentConfigManager> environmentConfigurationCache = new ConcurrentHashMap<>();

    private final Dotenv dotenv;
    private final String configName;

    private EnvironmentConfigManager(String configName, String envName) {
        this.configName = configName;
        try {
            this.dotenv = Dotenv.configure()
                    .directory(EnvironmentFilePaths.getDirectoryPath())
                    .filename(envName)
                    .load();
            logger.info("EnvironmentType '{}' was loaded successfully with alias '{}'", envName, configName);
        } catch (Exception error) {
            logger.error("Failed to load environment '{}' with alias '{}'", envName, configName);
            ErrorHandler.logError(error, "EnvironmentConfig Constructor", "Failed to load dotenv variables");
            throw new RuntimeException(error);
        }
    }

    public static void loadConfiguration(String configAlias, String envFilePath) {
        environmentConfigurationCache.computeIfAbsent(configAlias, key -> {
            try {
                logger.info("Loading environment configuration '{}' from '{}'", configAlias, envFilePath);
                return new EnvironmentConfigManager(configAlias, envFilePath);
            } catch (Exception error) {
                ErrorHandler.logError(error, "loadConfiguration", "Failed to load environment configuration");
                throw new RuntimeException(error);
            }
        });
    }

    public String getEnvironmentKey(String key) {
        try {
            String systemValue = System.getenv(key);
            if (systemValue != null) {
                logger.info("Using system environment variable for '{}': '{}'", key, systemValue);
                return systemValue;
            }

            String value = dotenv.get(key);
            if (value == null || value.isEmpty()) {
                String message = String.format("Environment variable '%s' not found or empty in configuration '%s'", key, configName);
                logger.warn(message);
                throw new IllegalArgumentException(message);
            }
            return value;
        } catch (Exception error) {
            ErrorHandler.logError(error, "getEnvironmentKey", "Failed to retrieve environment variable");
            throw new RuntimeException(error);
        }
    }

    public String getEnvironmentKey(String key, String defaultValue) {
        try {
            String systemValue = System.getenv(key);
            if (systemValue != null) {
                logger.info("Using system environment variable for '{}': '{}'", key, systemValue);
                return systemValue;
            }

            String value = dotenv.get(key, defaultValue);
            if (value.equals(defaultValue)) {
                logger.warn("Environment variable '{}' not found, using default '{}' in configuration '{}'", key, defaultValue, configName);
            } else {
                logger.info("Retrieved environment variable '{}' from configuration '{}'", key, configName);
            }
            return value;
        } catch (Exception error) {
            ErrorHandler.logError(error, "getEnv", "Failed to retrieve environment variable with default");
            throw new RuntimeException(error);
        }
    }

    public static EnvironmentConfigManager getConfiguration(String configAlias) {
        EnvironmentConfigManager config = environmentConfigurationCache.get(configAlias);
        if (config == null) {
            String message = String.format("Environment configuration '%s' not loaded. Call loadConfiguration first.", configAlias);
            logger.error(message);
            throw new IllegalStateException(message);
        }
        return config;
    }

    public static String getEnvironmentKeyFromCache (String aliasName, String environmentKey){
        try {
            return getConfiguration(aliasName).getEnvironmentKey(environmentKey);
        } catch (Exception error) {
            ErrorHandler.logError(error, "getCachedEnvironmentKey", "Failed to retrieve cached environment key");
            throw new RuntimeException(error);
        }
    }

    public static SecretKey getSecretKeyFromCache (String aliasName, String environmentSecretKey){
        try {
            return Base64Utils.decodeSecretKey(getConfiguration(aliasName).getEnvironmentKey(environmentSecretKey));

        } catch (Exception error) {
            ErrorHandler.logError(error, "getSecretKeyFromCache", "Failed to retrieve cached secret key");
            throw new RuntimeException(error);
        }
    }

    /**
     * Check if a configuration is loaded
     * @param configAlias Configuration alias to check
     * @return true if configuration is loaded, false otherwise
     */
    public static boolean isConfigurationLoaded(String configAlias) {
        try {
            return environmentConfigurationCache.containsKey(configAlias);
        } catch (Exception error) {
            ErrorHandler.logError(error, "isConfigurationLoaded", "Failed to check if configuration is loaded");
            throw new RuntimeException(error);
        }
    }

    /**
     * Get an environment key with type conversion
     * @param key Environment variable key
     * @param type Desired return type
     * @return Optional containing the converted value
     */
    public <ConversionType> Optional<ConversionType> getEnvironmentKey(String key, Class<ConversionType> type) {
        try {
            String systemValue = System.getenv(key);
            String value = systemValue != null ? systemValue : dotenv.get(key);

            if (value == null || value.isEmpty()) {
                logger.warn("Environment variable '{}' not found in configuration '{}'", key, configName);
                return Optional.empty();
            }

            // Type conversion
            ConversionType result = getConversionType(type, value);
            return Optional.of(result);
        } catch (Exception error) {
            ErrorHandler.logError(error, "getEnvironmentKey", "Failed to retrieve or convert environment variable");
            return Optional.empty();
        }
    }

    private <ConversionType> ConversionType getConversionType(Class<ConversionType> type, String value) {
        try{
        Object convertedValue = switch (type.getSimpleName()) {
            case "String" -> value;
            case "Integer" -> Integer.parseInt(value);
            case "Boolean" -> Boolean.parseBoolean(value);
            case "Double" -> Double.parseDouble(value);
            case "Long" -> Long.parseLong(value);
            default -> throw new UnsupportedOperationException("Unsupported type conversion");
        };

        @SuppressWarnings("unchecked")
        ConversionType result = (ConversionType) convertedValue;
        return result;
        } catch (Exception error) {
            ErrorHandler.logError(error, "getT", "Failed to convert property");
            throw new RuntimeException(error);
        }
    }

    public static synchronized void reloadConfiguration(String configAlias) {
        try {
            EnvironmentConfigManager existingConfig = environmentConfigurationCache.get(configAlias);
            if (existingConfig == null) {
                throw new IllegalStateException("Configuration '" + configAlias + "' not found. Load it first.");
            }

            // Remove and reload
            environmentConfigurationCache.remove(configAlias);
            loadConfiguration(configAlias, existingConfig.configName);
        } catch (Exception error) {
            ErrorHandler.logError(error, "reloadConfiguration", "Failed to reload configuration");
            throw new RuntimeException(error);
        }
    }

    /**
     * Get all loaded configuration aliases
     * @return Set of loaded configuration aliases
     */
    public static Set<String> getLoadedConfigurationAliases() {
        try {
            return Collections.unmodifiableSet(environmentConfigurationCache.keySet());
        } catch (Exception error) {
            ErrorHandler.logError(error, "getLoadedConfigurationAliases", "Failed to retrieve loaded configuration aliases");
            throw new RuntimeException(error);
        }
    }

}
