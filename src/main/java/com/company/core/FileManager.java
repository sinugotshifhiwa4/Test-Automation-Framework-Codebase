package com.company.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {

    /**
     * Creates a directory if it does not already exist.
     *
     * @param dirPath The path to the directory to create.
     * @throws IOException If the directory cannot be created due to an I/O error.
     */
    public static void createDirIfNotExists(String dirPath) throws IOException {
        if (dirPath == null || dirPath.isBlank()) {
            throw new IllegalArgumentException("Directory path cannot be null or empty.");
        }
        try {
            Path dir = Paths.get(dirPath);
            Files.createDirectories(dir);
        } catch (Exception error) {
            ErrorHandler.logError(
                    error,
                    "createDirIfNotExists",
                    "Failed to ensure directory exists: " + dirPath
            );
            throw error;
        }
    }

    /**
     * Creates a file in a specified directory if it does not already exist.
     *
     * @param dirPath  The path to the directory to create the file in.
     * @param fileName The name of the file to create.
     * @return The File object representing the created file.
     * @throws IOException If the file cannot be created due to an I/O error.
     */
    public static File createFileIfNotExists(String dirPath, String fileName) throws IOException {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name cannot be null or empty.");
        }
        try {
            createDirIfNotExists(dirPath);
            Path filePath = Paths.get(dirPath, fileName);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            return filePath.toFile();
        } catch (Exception error) {
            ErrorHandler.logError(
                    error,
                    "createFileIfNotExists",
                    "Failed to ensure file exists: " + fileName + " in " + dirPath
            );
            throw error;
        }
    }
}