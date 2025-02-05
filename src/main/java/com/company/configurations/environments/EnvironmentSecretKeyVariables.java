package com.company.configurations.environments;

public enum EnvironmentSecretKeyVariables {

    DEVELOPMENT("DEVELOPMENT_SECRET_KEY"),
    UAT("UAT_SECRET_KEY"),
    PRODUCTION("PRODUCTION_SECRET_KEY");

    private final String secretKeyVariable;

    EnvironmentSecretKeyVariables(String secretKeyVariable) {
        this.secretKeyVariable = secretKeyVariable;
    }

    public String getSecretKeyVariable() {
        return secretKeyVariable;
    }
}
