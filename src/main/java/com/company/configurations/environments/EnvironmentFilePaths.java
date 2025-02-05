package com.company.configurations.environments;

public enum EnvironmentFilePaths {
    BASE(".env"),
    DEVELOPMENT(".env.dev"),
    UAT(".env.uat"),
    PRODUCTION(".env.prod");

    private static final String ENV_DIRECTORY = "envs";
    private final String filename;

    EnvironmentFilePaths(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return ENV_DIRECTORY + "/" + filename;
    }

    public static String getDirectoryPath() {
        return ENV_DIRECTORY;
    }
}
