package com.company.configurations.properties;

public enum ConfigFileAlias {

    GLOBAL("GlobalConfig"),
    DEVELOPMENT("DevConfig"),
    UAT("UatConfig"),
    PRODUCTION("ProdConfig");

    private final String alias;

    ConfigFileAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }
}