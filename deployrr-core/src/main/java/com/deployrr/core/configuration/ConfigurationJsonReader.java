package com.deployrr.core.configuration;

import com.deployrr.api.configuration.DeployConfiguration;
import com.deployrr.api.configuration.ConfigurationReader;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;

public class ConfigurationJsonReader implements ConfigurationReader {

    private final Gson gson = new Gson();

    @Override
    public DeployConfiguration readConfiguration(Reader reader) throws IOException {
        try {
            return this.gson.fromJson(reader, DeployConfiguration.class);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

}
