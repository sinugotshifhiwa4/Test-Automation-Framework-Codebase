package com.company.tests.unit.environments;

import com.company.configurations.environments.EnvironmentConfigManager;
import com.company.configurations.environments.EnvironmentFileAlias;
import com.company.configurations.environments.EnvironmentFilePaths;
import com.company.core.ErrorHandler;
import com.company.utils.Base64Utils;
import com.company.utils.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.crypto.SecretKey;

public class EnvironmentConfigTests {

    private static final Logger logger = LoggerUtils.getLogger(EnvironmentConfigTests.class);

    private static final String USERNAME = "PORTAL_USERNAME";
    private static final String PASSWORD = "PORTAL_PASSWORD";
    private static final String UAT_SECRET_KEY = "UAT_SECRET_KEY";

    @Test(groups = {"configSetup"}, dataProvider = "EnvironmentConfigFiles")
    public void testEnvironmentConfig(EnvironmentFileAlias aliasName, EnvironmentFilePaths environmentFilePath){
        loadEnvironmentFileVariables(aliasName, environmentFilePath);
    }

    private void loadEnvironmentFileVariables(EnvironmentFileAlias aliasName, EnvironmentFilePaths environmentFilePath){
        try{
            // Load the environment config file
            EnvironmentConfigManager.loadConfiguration(aliasName.getAlias(), environmentFilePath.getFilename());

            // Log parameters based on the alias
            if (aliasName == EnvironmentFileAlias.BASE) {
                SecretKey uatSecretKey = EnvironmentConfigManager.getSecretKeyFromCache(aliasName.getAlias(), UAT_SECRET_KEY);
                String encodedUatSecretKey = Base64Utils.encodeSecretKey(uatSecretKey);
                logger.info("UAT secret key: {}", encodedUatSecretKey);

            } else if (aliasName == EnvironmentFileAlias.UAT) {
                String portalBaseUrl = EnvironmentConfigManager.getEnvironmentKeyFromCache(aliasName.getAlias(), USERNAME);
                String apiBaseUrl = EnvironmentConfigManager.getEnvironmentKeyFromCache(aliasName.getAlias(), PASSWORD);
                logger.info("Portal base URL: {}", portalBaseUrl);
                logger.info("API base URL: {}", apiBaseUrl);
            }

        } catch (Exception error){
            ErrorHandler.logError(error, "loadEnvironmentFileVariables", "Failed to load config file variables");
            throw new RuntimeException(error);
        }
    }

    @DataProvider(name = "EnvironmentConfigFiles")
    private Object[][] environmentConfigFiles() {
        return new Object[][]{
                {EnvironmentFileAlias.BASE, EnvironmentFilePaths.BASE},
                {EnvironmentFileAlias.UAT, EnvironmentFilePaths.UAT}
        };
    }
}
