package com.company.tests.unit.properties;

import com.company.configurations.properties.ConfigFileAlias;
import com.company.configurations.properties.ConfigFilePath;
import com.company.configurations.properties.PropertiesConfigManager;
import com.company.core.ErrorHandler;
import com.company.utils.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PropertiesConfigTests {

    private static final Logger logger = LoggerUtils.getLogger(PropertiesConfigTests.class);

    private static final String PROJECT_NAME = "PROJECT_NAME";
    private static final String REPORT_NAME = "REPORT_NAME";
    private static final String PORTAL_BASE_URL = "PORTAL_BASE_URL";
    private static final String API_BASE_URL = "API_BASE_URL";

    @Test(groups = {"configSetup"}, dataProvider = "PropertiesConfigFiles")
    public void testPropertiesConfig(ConfigFileAlias aliasName, ConfigFilePath configFilePath){
        loadConfigFileVariables(aliasName, configFilePath);
    }

    private void loadConfigFileVariables(ConfigFileAlias aliasName, ConfigFilePath configFilePath) {
        try{
            // Load the config file
            PropertiesConfigManager.loadConfiguration(aliasName.getAlias(), configFilePath.getFilePath());

            // Log parameters based on the alias
            if (aliasName == ConfigFileAlias.GLOBAL) {
                String projectName = PropertiesConfigManager.getPropertyKeyFromCache(aliasName.getAlias(), PROJECT_NAME);
                String reportName = PropertiesConfigManager.getPropertyKeyFromCache(aliasName.getAlias(), REPORT_NAME);

                logger.info("Project name: {}", projectName);
                logger.info("Report name: {}", reportName);

            } else if (aliasName == ConfigFileAlias.UAT) {
                String portalBaseUrl = PropertiesConfigManager.getPropertyKeyFromCache(aliasName.getAlias(), PORTAL_BASE_URL);
                String apiBaseUrl = PropertiesConfigManager.getPropertyKeyFromCache(aliasName.getAlias(), API_BASE_URL);

                logger.info("Portal base URL: {}", portalBaseUrl);
                logger.info("API base URL: {}", apiBaseUrl);

            } else {
                throw new IllegalArgumentException("Invalid alias: " + aliasName);
            }

        } catch (Exception error){
            ErrorHandler.logError(
                    error,
                    "loadConfigFileParams",
                    "Failed to load config files parameters"
            );
            throw new RuntimeException(error);
        }
    }

    @DataProvider(name = "PropertiesConfigFiles")
    private Object[][] propertiesConfigFiles() {
        return new Object[][]{
                {ConfigFileAlias.GLOBAL, ConfigFilePath.GLOBAL},
                {ConfigFileAlias.UAT, ConfigFilePath.UAT}
        };
    }
}
