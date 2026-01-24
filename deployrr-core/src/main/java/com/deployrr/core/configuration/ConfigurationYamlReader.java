package com.deployrr.core.configuration;

import com.deployrr.api.configuration.DeployConfiguration;
import com.deployrr.api.configuration.ConfigurationReader;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Reader;

public class ConfigurationYamlReader implements ConfigurationReader {

    private final Yaml yaml = new Yaml();

    @Override
    public DeployConfiguration readConfiguration(Reader reader) throws IOException {
        try {
            return this.yaml.loadAs(reader, DeployConfiguration.class);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

}
