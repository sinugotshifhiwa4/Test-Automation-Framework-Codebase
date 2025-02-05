package com.company.tests.encryption;

import com.company.configurations.environments.EnvironmentConfigManager;
import com.company.configurations.environments.EnvironmentFileAlias;
import com.company.configurations.environments.EnvironmentFilePaths;
import com.company.configurations.environments.EnvironmentSecretKeyVariables;
import com.company.core.ErrorHandler;
import com.company.crypto.utils.CryptoServiceUtils;
import com.company.utils.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.util.List;

public class DecryptTests {

    private static final Logger logger = LoggerUtils.getLogger(DecryptTests.class);

    private static final String USERNAME = "PORTAL_USERNAME";
    private static final String PASSWORD = "PORTAL_PASSWORD";

    @Test(groups = {"cryptoConfig"}, priority = 3)
    public void testDecryptProcess() {
        try {
            loadEnvironments();
            decryptCredentials();
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

    private void decryptCredentials() {
        try{
            List<String> decryptEnvironmentVariables = CryptoServiceUtils.decryptEnvironmentVariables(
                    EnvironmentFileAlias.UAT.getAlias(),
                    EnvironmentSecretKeyVariables.UAT.getSecretKeyVariable(),
                    USERNAME,
                    PASSWORD
            );

            logger.info("Decrypted Environment Variables: {}", decryptEnvironmentVariables);
            logger.info("Decrypted Username: {}", decryptEnvironmentVariables.get(0));
            logger.info("Decrypted Password: {}", decryptEnvironmentVariables.get(1));

            String decryptedPassword = CryptoServiceUtils.decryptEnvironmentVariable(
                    EnvironmentFileAlias.UAT.getAlias(),
                    EnvironmentSecretKeyVariables.UAT.getSecretKeyVariable(),
                    PASSWORD
            );

            logger.info("Decrypted Password: {}", decryptedPassword);

        } catch (Exception error) {
            ErrorHandler.logError(error, "decryptCredentials", "Failed to decrypt credentials");
            throw new RuntimeException(error);
        }
    }
}
