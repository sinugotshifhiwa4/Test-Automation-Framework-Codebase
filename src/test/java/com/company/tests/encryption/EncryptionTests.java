package com.company.tests.encryption;

import com.company.configurations.environments.EnvironmentConfigManager;
import com.company.configurations.environments.EnvironmentFileAlias;
import com.company.configurations.environments.EnvironmentFilePaths;
import com.company.configurations.environments.EnvironmentSecretKeyVariables;
import com.company.core.ErrorHandler;
import com.company.crypto.utils.CryptoServiceUtils;
import org.testng.annotations.Test;

public class EncryptionTests {

    private static final String USERNAME = "PORTAL_USERNAME";
    private static final String PASSWORD = "PORTAL_PASSWORD";

    @Test(groups = {"cryptoConfig"}, priority = 2)
    public void testEncryptionProcess() {
        try {
            loadEnvironments();
            encryptCredentials();
        } catch (Exception error) {
            ErrorHandler.logError(error, "testEncryptionProcess", "Failed to run encryption process test");
            throw new RuntimeException(error);
        }
    }

    private void loadEnvironments() {
        try {
            EnvironmentConfigManager.loadConfiguration(EnvironmentFileAlias.BASE.getAlias(), EnvironmentFilePaths.BASE.getFilename());
            EnvironmentConfigManager.loadConfiguration(EnvironmentFileAlias.UAT.getAlias(), EnvironmentFilePaths.UAT.getFilename());

        } catch (Exception error) {
            ErrorHandler.logError(error, "loadEnvironments", "Failed to load environments");
            throw new RuntimeException(error);
        }
    }

    private void encryptCredentials() {
        try {
            CryptoServiceUtils.encryptEnvironmentVariables(EnvironmentFilePaths.UAT.getPath(), EnvironmentFileAlias.UAT.getAlias(), EnvironmentSecretKeyVariables.UAT.getSecretKeyVariable(), USERNAME, PASSWORD);

        } catch (Exception error) {
            ErrorHandler.logError(error, "encryptCredentials", "Failed to encrypt credentials");
            throw new RuntimeException(error);
        }
    }
}
