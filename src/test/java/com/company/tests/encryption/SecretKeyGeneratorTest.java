package com.company.tests.encryption;

import com.company.configurations.environments.EnvironmentConfigManager;
import com.company.configurations.environments.EnvironmentFileAlias;
import com.company.configurations.environments.EnvironmentFilePaths;
import com.company.configurations.environments.EnvironmentSecretKeyVariables;
import com.company.core.ErrorHandler;
import com.company.crypto.services.SecureKeyGenerator;
import com.company.crypto.utils.CryptoServiceUtils;
import com.company.utils.Base64Utils;
import org.testng.annotations.Test;

import javax.crypto.SecretKey;

public class SecretKeyGeneratorTest {

    @Test(groups = {"cryptoConfig"}, priority = 1)
    public void testSecretKeyGenerator() {
        try {
            // Load the base environment
            EnvironmentConfigManager.loadConfiguration(EnvironmentFileAlias.BASE.getAlias(), EnvironmentFilePaths.BASE.getFilename());
            generateSecretKey();

        } catch (Exception error) {
            ErrorHandler.logError(error, "testSecretKeyGenerator", "Failed to generate secret key");
            throw new RuntimeException(error);
        }
    }

    private void generateSecretKey() {
        try{
            // Generate a new secret key
            SecretKey secretKey = SecureKeyGenerator.generateSecretKey();

            // Save the secret key in the base environment
            CryptoServiceUtils.saveSecretKeyInBaseEnvironment(
                    EnvironmentFilePaths.BASE.getPath(),
                    EnvironmentSecretKeyVariables.UAT.getSecretKeyVariable(),
                    Base64Utils.encodeSecretKey(secretKey));

        } catch (Exception error) {
            ErrorHandler.logError(error, "generateSecretKey", "Failed to generate secret key");
            throw new RuntimeException(error);
        }
    }
}
