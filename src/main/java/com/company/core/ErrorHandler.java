package com.company.core;

import com.company.utils.LoggerUtils;
import org.apache.logging.log4j.Logger;

public class ErrorHandler {

    private static final Logger logger = LoggerUtils.getLogger(ErrorHandler.class);

    // Prevent instantiation of utility class
    private ErrorHandler() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static <T extends Throwable> void logError(T error, String methodName, String errorMessage) {
        logError(error, methodName, errorMessage, null);
    }


    /**
     * Logs an error with detailed information and returns the error for further handling.
     *
     * @param error        The throwable instance representing the error (required)
     * @param methodName   The name of the method where the error occurred (required)
     * @param errorMessage Additional context about the error (optional)
     * @param context      Additional context information (optional)
     * @throws IllegalArgumentException if required parameters are null or invalid
     */
    public static <T extends Throwable> void logError(T error, String methodName, String errorMessage, String context) {
        validateParameters(error, "error");
        validateParameters(methodName, "methodName");

        String detailedMessage = buildErrorMessage(methodName, errorMessage, context, error);
        logger.error(detailedMessage, error);
    }

    private static void validateParameters(Object param, String paramName) {
        if (param == null) {
            throw new IllegalArgumentException(paramName + " cannot be null");
        }
        if (param instanceof String && ((String) param).isBlank()) {
            throw new IllegalArgumentException(paramName + " cannot be empty or blank");
        }
    }

    /**
     * Builds a detailed error message with additional context.
     *
     * @param methodName   The name of the method where the error occurred (required)
     * @param errorMessage Additional context about the error (optional)
     * @param context      Additional context information (optional)
     * @param error        The throwable instance representing the error (required)
     * @return A detailed error message with additional context
     */
    private static String buildErrorMessage(String methodName, String errorMessage, String context, Throwable error) {
        return String.format(
                "Error in method '%s': %s. Context: %s. Error details: %s",
                methodName,
                errorMessage != null ? errorMessage : "No additional error message provided",
                context != null ? context : "No additional context provided",
                error.getMessage()
        );
    }

    /**
     * Creates a custom exception instance with the provided detailed message.
     *
     * @param errorType       The custom exception type to create (optional)
     * @param detailedMessage The detailed message to pass to the exception constructor (required)
     * @return The created exception instance
     * @throws IllegalArgumentException if detailedMessage is null or empty
     * @throws T                        The created exception instance
     */
    private static <T extends RuntimeException> T createException(
            Class<T> errorType,
            String detailedMessage) {
        if (errorType == null) {
            @SuppressWarnings("unchecked")
            T runtimeException = (T) new RuntimeException(detailedMessage);
            return runtimeException;
        }

        try {
            return errorType.getConstructor(String.class).newInstance(detailedMessage);
        } catch (ReflectiveOperationException e) {
            logger.warn("Failed to create custom exception of type: {}. Defaulting to RuntimeException", errorType.getName());
            @SuppressWarnings("unchecked")
            T runtimeException = (T) new RuntimeException(detailedMessage);
            return runtimeException;
        }
    }
}
