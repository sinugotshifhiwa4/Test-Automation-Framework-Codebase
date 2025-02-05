package com.company.configurations.environments;

public enum EnvironmentType {

    BASE(".env"),
    DEVELOPMENT(".env.dev"),
    UAT(".env.uat"),
    PRODUCTION(".env.prod");

    private final String envFilePath;

    EnvironmentType(String envFilePath) {
        this.envFilePath = envFilePath;
    }

    public String getEnvironmentFilePath() {
        return envFilePath;
    }
}
