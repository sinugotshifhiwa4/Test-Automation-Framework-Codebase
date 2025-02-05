package com.company.configurations.environments;

public enum EnvironmentFileAlias {
    BASE("BaseEnvFile"),
    DEVELOPMENT("DevEnvFile"),
    UAT("UatEnvFile"),
    PRODUCTION("ProdEnvFile");

    private final String alias;

    EnvironmentFileAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }
}
