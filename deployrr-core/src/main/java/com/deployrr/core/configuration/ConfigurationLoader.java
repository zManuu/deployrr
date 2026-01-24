package com.deployrr.core.configuration;

import com.deployrr.api.configuration.DeployConfiguration;
import com.deployrr.api.configuration.DeployConfigurationReader;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ConfigurationLoader {

    private static final Logger LOG = LogManager.getLogger(ConfigurationLoader.class);

    private final DeployEnvInjector envInjector;
    private final File configurationFile;

    public ConfigurationLoader(DeployEnvInjector envInjector, File configurationFile) {
        this.envInjector = envInjector;
        this.configurationFile = configurationFile;
    }

    public DeployConfiguration loadConfiguration() throws IOException {
        LOG.info("Loading configuration.");
        DeployConfiguration deployConfiguration;

        if (this.configurationFile == null
                || !this.configurationFile.exists()
                || !this.configurationFile.isFile()) {
            throw new IOException("Passed configuration file does not exist or is not a file.");
        }

        // Read
        String configurationFileExt = FilenameUtils.getExtension(this.configurationFile.getName());
        DeployConfigurationReader configurationReader;
        switch (configurationFileExt) {
            case "json":
                configurationReader = new DeployConfigurationJsonReader();
                break;
            case "yml":
            case "yaml":
                configurationReader = new DeployConfigurationYamlReader();
                break;
            default:
                throw new IOException("Unsupported configuration file with extension '" + configurationFileExt + "'.");
        }
        try (FileReader fileReader = new FileReader(this.configurationFile)) {
            deployConfiguration = configurationReader.readConfiguration(fileReader);
        }

        // Inject ENV
        try {
            this.envInjector.setupEnv(deployConfiguration);
            this.envInjector.injectEnv(deployConfiguration);
            return deployConfiguration;
        } catch (Exception e) {
            throw new IOException("Could not inject ENV.", e);
        }
    }

}
