package com.company.configurations.properties;

public enum ConfigFilePath {
    GLOBAL("global-config.properties"),
    DEV("config-dev.properties"),
    UAT("config-uat.properties"),
    PROD("config-prod.properties");

    private final String path;
    private static final String CONTENT_ROOT_PATH = "src/main/resources/properties/";

    ConfigFilePath(String path) {
        this.path = path;
    }

    public String getFilePath() {
        return CONTENT_ROOT_PATH + path;
    }
}
