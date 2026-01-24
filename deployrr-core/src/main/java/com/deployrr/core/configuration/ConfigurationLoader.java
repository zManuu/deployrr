package com.deployrr.core.configuration;

import com.deployrr.api.configuration.DeployConfiguration;
import com.deployrr.api.configuration.ConfigurationReader;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ConfigurationLoader {

    private static final Logger LOG = LogManager.getLogger(ConfigurationLoader.class);

    private final ConfigurationEnvInjector envInjector;
    private final File configurationFile;

    public ConfigurationLoader(ConfigurationEnvInjector envInjector, File configurationFile) {
        this.envInjector = envInjector;
        this.configurationFile = configurationFile;
    }

    public DeployConfiguration loadConfiguration() throws IOException {
        if (this.configurationFile == null
                || !this.configurationFile.exists()
                || !this.configurationFile.isFile()) {
            throw new IOException("Passed configuration file does not exist or is not a file: " +
                    (this.configurationFile == null ? "null" : this.configurationFile.getPath()));
        }

        LOG.info("Loading configuration '{}'.", this.configurationFile.getPath());
        DeployConfiguration deployConfiguration;

        // Read
        String configurationFileExt = FilenameUtils.getExtension(this.configurationFile.getName());
        ConfigurationReader configurationReader;
        switch (configurationFileExt) {
            case "json":
                configurationReader = new ConfigurationJsonReader();
                break;
            case "yml":
            case "yaml":
                configurationReader = new ConfigurationYamlReader();
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
        } catch (Exception e) {
            throw new IOException("Could not inject ENV.", e);
        }

        // Load included files
        if (deployConfiguration.getIncludes() != null && !deployConfiguration.getIncludes().isEmpty()) {
            for (String include : deployConfiguration.getIncludes()) {
                File includeFile = new File(this.configurationFile.getParentFile(), include);
                ConfigurationLoader includeLoader = new ConfigurationLoader(this.envInjector, includeFile);
                DeployConfiguration includeConfiguration = includeLoader.loadConfiguration();
                this.mergeConfigurations(deployConfiguration, includeConfiguration);
            }
        }

        return deployConfiguration;
    }

    private void mergeConfigurations(DeployConfiguration configuration1, DeployConfiguration configuration2) {

        // SSH
        if (configuration1.getSsh() == null) {
            configuration1.setSsh(configuration2.getSsh());
        }

        // Tasks
        if (configuration1.getTasks() == null) {
            configuration1.setTasks(new ArrayList<>());
        }
        if (configuration2.getTasks() != null && !configuration2.getTasks().isEmpty()) {
            configuration1.getTasks().addAll(configuration2.getTasks());
        }

        // ENV
        if (configuration1.getVariables() == null) {
            configuration1.setVariables(new LinkedHashMap<>());
        }
        if (configuration2.getVariables() != null && !configuration2.getVariables().isEmpty()) {
            configuration1.getVariables().putAll(configuration2.getVariables());
        }

        // Set next includes
        if (configuration2.getIncludes() != null && !configuration2.getIncludes().isEmpty()) {
            configuration1.getIncludes().addAll(configuration2.getIncludes());
        }

    }

}
