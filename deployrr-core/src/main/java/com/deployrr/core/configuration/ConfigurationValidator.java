package com.deployrr.core.configuration;

import com.deployrr.api.configuration.DeployConfiguration;

import java.io.IOException;

public class ConfigurationValidator {

    public void validateConfiguration(DeployConfiguration configuration) throws IOException {
        assertExists(configuration, "configuration. Is the deployrr-file empty?");
        assertExists(configuration.getSsh(), "ssh");
        assertExists(configuration.getSsh().getUser(), "ssh.user");
        assertExists(configuration.getSsh().getHost(), "ssh.host");
        assertExists(configuration.getDeployrrVersion(), "deployrrVersion");
    }

    private void assertExists(Object object, String name) throws IOException {
        if (object == null) {
            throw new IOException("Missing: " + name);
        }
    }

}
