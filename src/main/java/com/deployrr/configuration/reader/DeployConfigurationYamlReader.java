package com.deployrr.configuration.reader;

import com.deployrr.configuration.DeployConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Reader;

public class DeployConfigurationYamlReader implements DeployConfigurationReader {

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
